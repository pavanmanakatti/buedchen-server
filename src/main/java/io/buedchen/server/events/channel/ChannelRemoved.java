package io.buedchen.server.events.channel;

import java.util.Objects;

public class ChannelRemoved {

    private final String channelId;

    public ChannelRemoved(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelRemoved that = (ChannelRemoved) o;
        return Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId);
    }

    public String getChannelId() {
        return channelId;
    }

}
