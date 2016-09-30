package io.buedchen.server.events.content;

import io.buedchen.server.Content;

import java.util.Objects;

public class ChannelContentAdded {

    private final String channelId;
    private final Content content;

    public ChannelContentAdded(String channelId, Content content) {
        this.channelId = channelId;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelContentAdded that = (ChannelContentAdded) o;
        return Objects.equals(channelId, that.channelId) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, content);
    }

    public String getChannelId() {
        return channelId;
    }

    public Content getContent() {
        return content;
    }

}
