package io.ullrich.buedchen.server.events.channel;

import io.ullrich.buedchen.server.Channel;

public class ChannelCreated {

    private final Channel channel;

    public ChannelCreated(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

}
