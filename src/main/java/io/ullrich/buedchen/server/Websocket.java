package io.ullrich.buedchen.server;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/schedule/{clientId}")
public class Websocket {

    private static final Logger logger = LoggerFactory.getLogger(Websocket.class);

    private final EventBusWrapper eventBus;

    public Websocket(EventBusWrapper eventBus) {
        this.eventBus = eventBus;

    }

    @OnClose
    public void onWebSocketClose(@PathParam("clientId") String clientId, Session session, CloseReason close) {
//        eventBus.post(new ClientDisconnected(clientId, session));
        logger.debug("Client {} disconnected: {} - {}", clientId, close.getCloseCode(), close.getReasonPhrase());
    }

    @OnOpen
    public void onWebSocketOpen(@PathParam("clientId") String clientId, Session session) {
        logger.debug("WebSocket Connect: {}", session);
        session.getAsyncRemote().sendText("Welcome " + clientId);
        //     eventBus.post(new ClientConnected(clientId, session));
    }

    @OnError
    public void onWebSocketError(@PathParam("clientId") String clientId, Throwable cause) {
        logger.warn("WebSocket Error: {}", clientId, cause);
    }

    @OnMessage
    public void onWebSocketText(@PathParam("clientId") String clientId, String message) {
        logger.info("Received message from {}: {}", clientId, message);
    }

}
