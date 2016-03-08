package io.ullrich.buedchen.server.events.client;

import javax.websocket.Session;

public class ClientDisconnected {

    private final String clientId;
    private final Session session;

    public ClientDisconnected(String clientId, Session session) {
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
