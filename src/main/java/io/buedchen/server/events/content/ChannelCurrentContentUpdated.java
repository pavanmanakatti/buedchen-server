package io.buedchen.server.events.content;

import io.buedchen.server.Content;

public class ChannelCurrentContentUpdated {

    private final String clientId;
    private final Content content;

    public ChannelCurrentContentUpdated(String clientId, Content content) {
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
