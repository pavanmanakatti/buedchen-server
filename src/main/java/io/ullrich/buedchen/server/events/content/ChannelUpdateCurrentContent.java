package io.ullrich.buedchen.server.events.content;

public class ChannelUpdateCurrentContent {

    private final String channelId;

    public ChannelUpdateCurrentContent(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

}
