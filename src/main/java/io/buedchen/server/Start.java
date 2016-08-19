package io.buedchen.server;

import io.buedchen.server.listeners.*;
import io.buedchen.server.persistence.Persistence;
import io.buedchen.server.persistence.PersistenceObject;
import io.buedchen.server.Storage.H2DatabaseStorage;
import io.buedchen.server.Storage.Storage;
import io.buedchen.server.events.PersistData;
import io.buedchen.server.events.ServerStarted;
import io.buedchen.server.events.client.PingClient;
import io.buedchen.server.events.content.StartContentUpdates;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Start {

    private static final Logger logger = LoggerFactory.getLogger(Start.class);

    private Channels channels;
    private Clients clients;
    private EventBusWrapper eventBus;

    private Projects projects;

    public Start() {

        //setup domain
        ResourcesSingleton singleton = ResourcesSingleton.getInstance();

        this.channels = singleton.getChannels();

        this.clients = singleton.getClients();
        this.eventBus = singleton.getEventBus();
        this.projects = singleton.getProjects();
        //load data
        loadData();

        //wire listeners
        eventBus.register(new DeadEventListener());
        eventBus.register(new ClientListeners(eventBus, this.channels, this.clients));
        eventBus.register(new ChannelListeners(eventBus, this.channels, this.clients));
        eventBus.register(new ChannelContentListeners(eventBus, this.channels, this.clients));
        eventBus.register(new PersistenceDatabaseListeners());
        eventBus.register(new PersistenceListeners(eventBus, this.channels, this.clients, this.projects));
        eventBus.post(new PersistData());
        eventBus.post(new StartContentUpdates());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                eventBus.post(new PingClient());
            }
        }, 10, 10, TimeUnit.SECONDS);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                new DashboardStatus().updateStatus();
            }
        }, 2, 30, TimeUnit.SECONDS);

        Server server = null;
        server = new BuedchenServer.Builder().restContext().swaggerUIContext().wsContext().build(8080);
        updateDatabase();

        try {
            server.start();
            eventBus.post(new ServerStarted());
            server.join();
        } catch (Exception e) {
            logger.error("Error: ", e);
        } finally {
            server.destroy();
        }
    }

    public static void main(String[] args) {
        new Start();

    }

    private void loadData() {
        try {
            PersistenceObject persistence = Persistence.read();
            for (Map.Entry<String, String> persistedChannel : persistence.getChannels().entrySet()) {
                String channelId = persistedChannel.getKey();
                String channelDescription = persistedChannel.getValue();
                channels.addChannel(new Channel(channelId, channelDescription));
            }
            for (Map.Entry<String, List<Content>> persistedContents : persistence.getContents().entrySet()) {
                String channelId = persistedContents.getKey();
                for (Content content : persistedContents.getValue()) {
                    channels.addContentToChannel(channelId, content);
                }
            }
            for (Map.Entry<String, String> persistedClients : persistence.getClients().entrySet()) {
                String clientId = persistedClients.getKey();
                String description = persistedClients.getValue();
                clients.addClient(clientId, description);
            }

            for (Map.Entry<String, String> persistedProject : persistence.getProjects().entrySet()) {
                String projectId = persistedProject.getKey();
                String projectDescription = persistedProject.getValue();
                projects.addProject(new Project(projectId, projectDescription));
            }

            for (Map.Entry<String, List<Dashboard>> persistedProjectContents : persistence.getProjectContents()
                    .entrySet()) {
                String projectId = persistedProjectContents.getKey();
                for (Dashboard persistedProjectContent : persistedProjectContents.getValue()) {
                    projects.addDashboardToProject(projectId, persistedProjectContent);
                }
            }

        } catch (FileNotFoundException e) {
            logger.info("No persisted data found - starting fresh");
            channels.addChannel(new Channel("UNASSIGNED", "CLIENT NOT ASSIGNED"));
            Content content = null;
            content = new Content(
                    "https://docs.google.com/presentation/d/1nvaobOFrc7fjmQhXjYxzfWR2CzMCH1Ar1QY_9VtYzqY/pub?start=true&loop=true&delayms=10000",
                    120, "Unassigned", "Unassigned Channel", "Unassigned");
            channels.addContentToChannel("UNASSIGNED", content);
        } catch (IOException e) {
            logger.error("Error reading persisted data - exiting");
            System.exit(1);
        }
    }

    private void updateDatabase() {

        Storage ds = new H2DatabaseStorage();
        ds.persistChannels(channels);
        ds.persistClients(clients);
        ds.persistProjects(projects);
    }
}
