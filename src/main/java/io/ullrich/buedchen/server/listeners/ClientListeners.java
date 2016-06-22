package io.ullrich.buedchen.server.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import io.ullrich.buedchen.server.*;
import io.ullrich.buedchen.server.events.client.*;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientListeners {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ClientListeners.class);

    private final EventBusWrapper eventBus;
    private final Clients clients;
    private final Channels channels;
    private final Map<String, List<Session>> sessions = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();

    public ClientListeners(EventBusWrapper eventBus, Channels channels, Clients clients) {
        this.eventBus = eventBus;
        this.clients = clients;
        this.channels = channels;
    }

    @Subscribe
    public void clientAssigned(ClientAssigned clientAssigned) {
        String channelId = this.clients.getClient(clientAssigned.getClientId()).getChannelId();
        if (this.channels.getChannels().containsKey(channelId) && !this.channels.getChannelContents(channelId).isEmpty()) {
            Content content = this.channels.getChannelContents(channelId).get(this.channels.getChannel(channelId).getContentPtr());
            this.eventBus.post(new UpdateClientContent(clientAssigned.getClientId(), content));
        }
    }

    @Subscribe
    public void clientConnected(ClientConnected clientConnected) {
        if(!sessions.containsKey(clientConnected.getClientId())) {
            sessions.put(clientConnected.getClientId(), new ArrayList<>());
        }

        sessions.get(clientConnected.getClientId()).add(clientConnected.getSession());

        if (!this.clients.getClientIds().contains(clientConnected.getClientId())) {
            this.clients.addClient(clientConnected.getClientId());
        }

        String channelId = this.clients.getClient(clientConnected.getClientId()).getChannelId();
        if (this.channels.getChannels().containsKey(channelId) && !this.channels.getChannelContents(channelId).isEmpty()) {
            Content content = this.channels.getChannelContents(channelId).get(this.channels.getChannel(channelId).getContentPtr());
            this.eventBus.post(new UpdateClientContent(clientConnected.getClientId(), content));
        } else {
            channelId = "UNASSIGNED";
            Content content = this.channels.getChannelContents(channelId).get(this.channels.getChannel(channelId).getContentPtr());
            this.eventBus.post(new UpdateClientContent(clientConnected.getClientId(), content));
        }
    }

    @Subscribe
    public void clientDisconnected(ClientDisconnected clientDisconnected) {
        sessions.get(clientDisconnected.getClientId()).remove(clientDisconnected.getSession());
    }

    @Subscribe
    public void assignClient(AssignClient assignClient) {
        if (this.clients.getClientIds().contains(assignClient.getClientId())) {
            this.clients.addClient(assignClient.getClientId());
        }
        String channelId = this.clients.getClient(assignClient.getClientId()).getChannelId();
        if (this.channels.getChannels().containsKey(channelId) && !this.channels.getChannelContents(channelId).isEmpty()) {
            Content content = this.channels.getChannelContents(channelId).get(this.channels.getChannel(channelId).getContentPtr());
            this.eventBus.post(new UpdateClientContent(assignClient.getClientId(), content));
        }
    }

    @Subscribe
    public void messageClientToContent(UpdateClientContent updateClientContent) {
        if (!sessions.containsKey(updateClientContent.getClientId())) {
            return;
        }
        for (Session session : sessions.get(updateClientContent.getClientId())) {
            try {
                EventWrapper wrapper = new EventWrapper("content", updateClientContent.getContent());
                session.getAsyncRemote().sendText(mapper.writeValueAsString(wrapper));
            } catch (JsonProcessingException ex) {
                logger.error("Could not convert content to json", ex);
            }
        }
    }

    @Subscribe
    public void pingClient(PingClient pingClient) {
        for (String clientId : sessions.keySet()) {
            for (Session session : sessions.get(clientId)) {
                EventWrapper wrapper = new EventWrapper("ping", "empty");
                try {
                    session.getAsyncRemote().sendText(mapper.writeValueAsString(wrapper));
                } catch (JsonProcessingException ex) {
                    logger.error("Could not convert content to json", ex);
                }
            }
        }
    }
}
