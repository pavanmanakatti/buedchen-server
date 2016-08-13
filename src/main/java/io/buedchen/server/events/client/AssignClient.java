package io.buedchen.server.events.client;

import io.buedchen.server.Content;

public class AssignClient {

    private final String clientId;
    private final Content content;

    public AssignClient(String clientId, Content content) {
        this.clientId = clientId;
        this.content = content;
    }

    public String getClientId() {
        return clientId;
    }

    public Content getContent() {
        return content;
    }
}
