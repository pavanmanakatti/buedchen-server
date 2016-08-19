package io.buedchen.server;

import io.buedchen.server.events.client.ClientDisconnected;
import io.buedchen.server.events.client.ClientConnected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint(value = "/schedule/{clientId}")
public class Websocket {

    private static final Logger logger = LoggerFactory.getLogger(Websocket.class);
    private final EventBusWrapper eventBus;
    private ResourcesSingleton resources;

    public Websocket() {
        resources = ResourcesSingleton.getInstance();
        this.eventBus = resources.getEventBus();
    }

    @OnClose
    public void onWebSocketClose(@PathParam("clientId") String clientId, Session session, CloseReason close) {
        logger.debug("Client {} disconnected: {} - {}", clientId, close.getCloseCode(), close.getReasonPhrase());
        eventBus.post(new ClientDisconnected(clientId, session));
    }

    @OnOpen
    public void onWebSocketOpen(@PathParam("clientId") String clientId, Session session) {
        logger.debug("WebSocket Connect: {}", session);
        eventBus.post(new ClientConnected(clientId, session));
    }

    @OnError
    public void onWebSocketError(@PathParam("clientId") String clientId, Session session, Throwable cause) {
        logger.warn("WebSocket Error: {}", clientId, cause);
        eventBus.post(new ClientDisconnected(clientId, session));

    }

    @OnMessage
    public void onWebSocketText(@PathParam("clientId") String clientId, String message) {
        logger.info("Received message from {}: {}", clientId, message);
    }

}
