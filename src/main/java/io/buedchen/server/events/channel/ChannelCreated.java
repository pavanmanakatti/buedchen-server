package io.buedchen.server.events.channel;

import io.buedchen.server.Channel;

import java.util.Objects;

public class ChannelCreated {

    private final Channel channel;

    public ChannelCreated(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelCreated that = (ChannelCreated) o;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}
