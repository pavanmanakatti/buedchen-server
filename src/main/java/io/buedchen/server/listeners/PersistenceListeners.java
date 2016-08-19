package io.buedchen.server.listeners;

import com.google.common.eventbus.Subscribe;
import io.buedchen.server.*;
import io.buedchen.server.persistence.PersistenceObject;
import io.buedchen.server.*;
import io.buedchen.server.events.PersistData;
import io.buedchen.server.events.channel.ChannelCreated;
import io.buedchen.server.events.channel.ChannelRemoved;
import io.buedchen.server.events.channel.ChannelUpdated;
import io.buedchen.server.events.client.ClientAssigned;
import io.buedchen.server.events.content.ChannelContentAdded;
import io.buedchen.server.events.content.ChannelContentRemoved;
import io.buedchen.server.events.content.ChannelContentUpdated;
import io.buedchen.server.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistenceListeners {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceListeners.class);

    private final EventBusWrapper eventBus;
    private final Clients clients;
    private final Channels channels;
    private final Projects projects;

    public PersistenceListeners(EventBusWrapper eventBus, Channels channels, Clients clients, Projects projects) {
        this.eventBus = eventBus;
        this.clients = clients;
        this.channels = channels;
        this.projects = projects;
    }

    @Subscribe
    public void startDataPersistence(PersistData persistData) {
        persistData();
    }

    @Subscribe
    public void channelContentAdded(ChannelContentAdded channelContentAdded) {
        persistData();
    }

    @Subscribe
    public void channelContentRemoved(ChannelContentRemoved channelContentRemoved) {
        persistData();
    }

    @Subscribe
    public void assignClient(ClientAssigned clientAssigned) {
        persistData();
    }

    @Subscribe
    public void channelCreated(ChannelCreated channelCreated) {
        persistData();

    }

    @Subscribe
    public void channelRemoved(ChannelRemoved channelRemoved) {
        persistData();

    }

    @Subscribe
    public void channelUpdated(ChannelUpdated channelUpdated) {
        persistData();
    }

    @Subscribe
    public void channelContentUpdated(ChannelContentUpdated channelContentUpdated) {
        persistData();
    }

    private void persistData() {
        logger.info("Persisting data");
        ResourcesSingleton resources = ResourcesSingleton.getInstance();
        PersistenceObject po = new PersistenceObject();

        Map<String, String> poClients = new HashMap<>();
        Map<String, String> poChannels = new HashMap<>();
        Map<String, List<Content>> poContents = new HashMap<>();
        Map<String, String> poProjects = new HashMap<>();
        Map<String, List<Dashboard>> poDashboards = new HashMap<>();

        for (String clientId : this.clients.getClientIds()) {
            poClients.put(clientId, this.clients.getClient(clientId).getChannelId());
        }
        po.setClients(poClients);

        for (Channel channel : this.channels.getChannels().values()) {
            poChannels.put(channel.getChannelId(), channel.getChannelDescription());
            List<Content> contentList = new ArrayList<Content>(this.channels.getChannelContents(channel.getChannelId()).values());
            poContents.put(channel.getChannelId(), contentList);
        }
        po.setChannels(poChannels);
        po.setContents(poContents);

        for (Project project : this.projects.getProjects().values()) {
            poProjects.put(project.getProjectId(), project.getProjectDescription());
            List<Dashboard> dashboardList = new ArrayList<Dashboard>(this.projects.getProjectDashboards(project.getProjectId()).values());
            poDashboards.put(project.getProjectId(), dashboardList);
        }
        po.setProjects(poProjects);
        po.setProjectContents(poDashboards);

        try {
            Persistence.save(po);
        } catch (IOException e) {
            logger.error("COULD NOT PERSIST DATA");
        }

    }
}
