package io.buedchen.server;

import io.buedchen.server.exceptions.ChannelAlreadyExistsException;
import io.buedchen.server.exceptions.ChannelNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Channels {

    private final Map<String, Channel> channels = Collections.synchronizedMap(new HashMap<>());

    public Channels() {
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public Channel getChannel(String channel) throws ChannelNotFoundException {
        ensureChannelExists(channel);
        return channels.get(channel);
    }

    public void addChannel(Channel channel) throws ChannelAlreadyExistsException {
        ensureChannelDoesNotExists(channel.getChannelId());
        channels.put(channel.getChannelId(), channel);
    }

    public void addContentToChannel(String channelId, Content content) {
        ensureChannelExists(channelId);
        if (!getChannel(channelId).getContents().containsKey(content.getUrl())) {
            getChannel(channelId).addContent(content);
            //todo restart etc.
        }
    }

    public Map<String, Content> getChannelContents(String channelId) {
        ensureChannelExists(channelId);
        return this.channels.get(channelId).getContents();
    }

    public void updateChannelContent(String channelId, String url, Content content) {
        this.channels.get(channelId).updateContent(url, content);
    }

    public void removeContentFromChannel(String channelId, Content content) {
        ensureChannelExists(channelId);
        getChannel(channelId).removeContent(content);
    }

    private void ensureChannelExists(String channelId) throws ChannelNotFoundException {
        if (!channels.containsKey(channelId)) {
            throw new ChannelNotFoundException("Channel does not exist");
        }
    }

    private void ensureChannelDoesNotExists(String channelId) throws ChannelAlreadyExistsException {
        if (channels.containsKey(channelId)) {
            throw new ChannelAlreadyExistsException("Channel already exists");
        }
    }

}
