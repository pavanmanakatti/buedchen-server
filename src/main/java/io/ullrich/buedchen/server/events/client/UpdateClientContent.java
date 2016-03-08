package io.ullrich.buedchen.server.events.client;

import io.ullrich.buedchen.server.Content;

public class UpdateClientContent {

    private final String clientId;
    private final Content content;

    public UpdateClientContent(String clientId, Content content) {
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
