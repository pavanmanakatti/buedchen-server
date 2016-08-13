package io.buedchen.server.events.channel;

import io.buedchen.server.Channel;

public class ChannelUpdated {

    private final Channel channel;

    public ChannelUpdated(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

}
