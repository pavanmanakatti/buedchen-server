package io.buedchen.server.events.content;

import io.buedchen.server.Content;

import java.util.Objects;

public class ChannelContentUpdated {

    private final String channelId;
    private final String contentUrl;
    private final Content content;

    public ChannelContentUpdated(String channelId, String contentUrl, Content content) {
        this.channelId = channelId;
        this.contentUrl = contentUrl;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelContentUpdated that = (ChannelContentUpdated) o;
        return Objects.equals(channelId, that.channelId) &&
                Objects.equals(contentUrl, that.contentUrl) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, contentUrl, content);
    }

    public String getChannelId() {
        return channelId;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public Content getContent() {
        return content;
    }

}
