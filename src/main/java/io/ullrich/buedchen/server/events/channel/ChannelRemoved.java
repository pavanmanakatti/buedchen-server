package io.ullrich.buedchen.server.events.channel;

public class ChannelRemoved {

    private final String channelId;

    public ChannelRemoved(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

}
