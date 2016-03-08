package io.ullrich.buedchen.server;

import io.ullrich.buedchen.server.exceptions.ChannelAlreadyExistsException;
import io.ullrich.buedchen.server.exceptions.ChannelNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Channels {

    private final Map<String, Channel> channels = Collections.synchronizedMap(new HashMap<String, Channel>());

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
        if (!getChannel(channelId).getContents().contains(content)) {
            getChannel(channelId).getContents().add(content);
            //todo restart etc.
        }
    }

    public List<Content> getChannelContents(String channelId) {
        ensureChannelExists(channelId);
        return this.channels.get(channelId).getContents();
    }

    public void removeContentFromChannel(String channelId, Content content) {
        ensureChannelExists(channelId);
        if (getChannelContents(channelId).contains(content)) {
            getChannelContents(channelId).remove(content);
        }
    }

    /*    public void addContent(Content content) {
        this.contents.add(content);
        if (this.contents.size() == 1) {
            this.contentPtr.set(0);
            scheduleContentChange(content.getShowtime());
        }
    }*/
 /*

    public Content getCurrentContent() {
        return this.contents.get(this.contentPtr.get());
    }

    private Integer getNextContentPointer() {
        return (this.contentPtr.get() + 1) % this.contents.size();
    }

    public Content getNextContent() {
        return this.contents.get(getNextContentPointer());
    }

    public void updateCurrentContent() {
        Content next = getNextContent();
        this.contentPtr.set(getNextContentPointer());
        scheduleContentChange(next.getShowtime());
    }

    private void scheduleContentChange(Integer showtime) {
        this.contentUpdateFuture = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                updateCurrentContent();
            }
        }, showtime, SECONDS);
    }*/
    private void ensureChannelExists(String channelId) throws ChannelNotFoundException {
        if (!channels.containsKey(channelId)) {
            throw new ChannelNotFoundException("Channel does not exist");
        }
    }

    private void ensureChannelDoesNotExists(String channelId) throws ChannelAlreadyExistsException {
        if (channels.containsKey(channelId)) {
            throw new ChannelAlreadyExistsException("Channel does not exist");
        }
    }

}
