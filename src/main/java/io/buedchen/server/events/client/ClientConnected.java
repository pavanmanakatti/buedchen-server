package io.buedchen.server.events.client;

import javax.websocket.Session;

public class ClientConnected {

    private final String clientId;
    private final Session session;

    public ClientConnected(String clientId, Session session) {
        this.clientId = clientId;
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public String getClientId() {
        return clientId;
    }

}
