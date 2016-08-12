package io.buedchen.server.events.content;

import io.buedchen.server.Content;

public class ChannelContentUpdated {

    private final String channelId;
    private final Integer contentId;
    private final Content content;

    public ChannelContentUpdated(String channelId, Integer contentId, Content content) {
        this.channelId = channelId;
        this.contentId = contentId;
        this.content = content;
    }

    public String getChannelId() {
        return channelId;
    }

    public Integer getContentId() {
        return contentId;
    }

    public Content getContent() {
        return content;
    }

}
