package io.buedchen.server.events.content;

import io.buedchen.server.Content;

public class ChannelRemoveContent {

    private final String channelId;
    private final Content content;

    public ChannelRemoveContent(String channelId, Content content) {
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
