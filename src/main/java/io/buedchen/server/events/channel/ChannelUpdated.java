package io.buedchen.server.events.channel;

import io.buedchen.server.Channel;

import java.util.Objects;

public class ChannelUpdated {

    private final Channel channel;

    public ChannelUpdated(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelUpdated that = (ChannelUpdated) o;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }

    public Channel getChannel() {
        return channel;
    }

}
