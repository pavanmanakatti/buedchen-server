package io.buedchen.server.events.content;

import io.buedchen.server.Content;

public class ChannelContentAdded {

    private final String channelId;
    private final Content content;

    public ChannelContentAdded(String channelId, Content content) {
        this.channelId = channelId;
        this.content = content;
    }

    public String getChannelId() {
        return channelId;
    }

    public Content getContent() {
        return content;
    }

}
