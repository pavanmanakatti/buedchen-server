package io.buedchen.server.events.content;

import io.buedchen.server.Content;

import java.util.Objects;

public class ChannelContentRemoved {

    private final String channelId;
    private final Content content;
    private final String contentUrl;

    public ChannelContentRemoved(String channelId, Content content, String contentUrl) {
        this.channelId = channelId;
        this.content = content;
        this.contentUrl = contentUrl;
    }

    public String getChannelId() {
        return channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelContentRemoved that = (ChannelContentRemoved) o;
        return Objects.equals(channelId, that.channelId) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, content);
    }

    public Content getContent() {
        return content;
    }

    public String getContentUrl() {
        return contentUrl;
    }

}
