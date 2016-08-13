package io.buedchen.server;

import com.google.common.eventbus.EventBus;
import io.buedchen.server.events.PersistData;
import io.buedchen.server.events.ServerStarted;
import io.buedchen.server.listeners.*;
import io.buedchen.server.persistence.PersistenceObject;
import io.buedchen.server.events.client.PingClient;
import io.buedchen.server.events.content.StartContentUpdates;
import io.buedchen.server.persistence.Persistence;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Start {

    private static final Logger logger = LoggerFactory.getLogger(Start.class);

    private Channels channels;
    private Clients clients;

    public Start() {
        EventBusWrapper eventBus = new EventBusWrapper(new EventBus());
        eventBus.register(new DeadEventListener());

        //setup domain
        ResourcesSingleton singleton = ResourcesSingleton.getInstance();
        this.channels = new Channels();
        this.clients = new Clients();

        //load data
        loadData();

        singleton.setChannels(this.channels);
        singleton.setClients(this.clients);
        singleton.setEventBus(eventBus);

        //wire listeners
        eventBus.register(new ClientListeners(eventBus, this.channels, this.clients));
        eventBus.register(new ChannelListeners(eventBus, this.channels, this.clients));
        eventBus.register(new ChannelContentListeners(eventBus, this.channels, this.clients));
        eventBus.register(new PersistenceListeners(eventBus, this.channels, this.clients));

        eventBus.post(new PersistData());
        eventBus.post(new StartContentUpdates());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                eventBus.post(new PingClient());
            }
        }, 10, 10, TimeUnit.SECONDS);

        //setup jetty etc.
        Server server = new Server(8080);

        try {
            ServletContextHandler wsContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
            wsContext.setContextPath("/ws");

            ServletContextHandler restContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            restContext.setContextPath("/api");

            HandlerCollection handlers = new HandlerCollection();
            handlers.addHandler(wsContext);
            handlers.addHandler(restContext);
            server.setHandler(handlers);

            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(wsContext);
            wscontainer.addEndpoint(Websocket.class);

            ServletHolder restServlet = restContext.addServlet(ServletContainer.class, "/*");
            restServlet.setInitOrder(0);
            restServlet.setInitParameter("jersey.config.server.provider.classnames", REST.class.getCanonicalName());

            FilterHolder filterHolder = new FilterHolder();
            filterHolder.setFilter(new CrossOriginFilter());
            restContext.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

            server.start();
            eventBus.post(new ServerStarted());
            server.join();
        } catch (Exception ex) {
            logger.error("Could not start server", ex);
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
                logger.info("omg");
                String channelId = persistedContents.getKey();
                for (Content persistedContent : persistedContents.getValue()) {
                    channels.addContentToChannel(channelId, persistedContent);
                }
            }
            for (Map.Entry<String, String> persistedClients : persistence.getClients().entrySet()) {
                String clientId = persistedClients.getKey();
                String description = persistedClients.getValue();
                clients.addClient(clientId, description);
            }
        } catch (FileNotFoundException e) {
            logger.info("No persisted data found - starting fresh");
            channels.addChannel(new Channel("UNASSIGNED", "CLIENT NOT ASSIGNED"));
            Content content = new Content("https://docs.google.com/presentation/d/1nvaobOFrc7fjmQhXjYxzfWR2CzMCH1Ar1QY_9VtYzqY/pub?start=true&loop=true&delayms=10000", 120, "Unassigned Channel", "Unassigned");
            channels.addContentToChannel("UNASSIGNED", content);
        } catch (IOException e) {
            logger.error("Error reading persisted data - exiting");
            System.exit(1);
        }
    }
}
