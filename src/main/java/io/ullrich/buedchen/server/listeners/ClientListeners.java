package io.ullrich.buedchen.server.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import io.ullrich.buedchen.server.Channels;
import io.ullrich.buedchen.server.Clients;
import io.ullrich.buedchen.server.Content;
import io.ullrich.buedchen.server.EventBusWrapper;
import io.ullrich.buedchen.server.events.client.AssignClient;
import io.ullrich.buedchen.server.events.client.ClientAssigned;
import io.ullrich.buedchen.server.events.client.ClientConnected;
import io.ullrich.buedchen.server.events.client.ClientDisconnected;
import io.ullrich.buedchen.server.events.client.UpdateClientContent;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.Session;
import org.slf4j.LoggerFactory;

public class ClientListeners {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ClientListeners.class);

    private final EventBusWrapper eventBus;
    private final Clients clients;
    private final Channels channels;
    private final Map<String, Session> sessions = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();

    public ClientListeners(EventBusWrapper eventBus, Channels channels, Clients clients) {
        this.eventBus = eventBus;
        this.clients = clients;
        this.channels = channels;
    }

    @Subscribe
    public void clientAssigned(ClientAssigned clientAssigned) {

    }

    @Subscribe
    public void clientConnected(ClientConnected clientConnected) {
        sessions.put(clientConnected.getClientId(), clientConnected.getSession());
        if (this.clients.getClientIds().contains(clientConnected.getClientId())) {
            this.clients.addClient(clientConnected.getClientId());
        }
        String channelId = this.clients.getClient(clientConnected.getClientId()).getChannelId();
        if (this.channels.getChannels().containsKey(channelId) && !this.channels.getChannelContents(channelId).isEmpty()) {
            Content content = this.channels.getChannelContents(channelId).get(this.channels.getChannel(channelId).getContentPtr());
            this.eventBus.post(new UpdateClientContent(clientConnected.getClientId(), content));
        }
    }

    @Subscribe
    public void clientDisconnected(ClientDisconnected clientDisconnected) {
        sessions.remove(clientDisconnected.getClientId());
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
    public void messageClient(UpdateClientContent updateClientContent) {
        Session session = sessions.get(updateClientContent.getClientId());
        try {
            session.getAsyncRemote().sendText(mapper.writeValueAsString(updateClientContent.getContent()));
        } catch (JsonProcessingException ex) {
            logger.error("Could not convert content to json", ex);
        }
    }

}
