package io.buedchen.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import org.apache.http.annotation.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Channel {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("channel_description")
    private String channelDescription;
    @JsonIgnore
    private final Map<String, Content> contents = Collections.synchronizedMap(new HashMap<String, Content>());
    @JsonIgnore
    private AtomicInteger contentPtr = new AtomicInteger();
    @JsonIgnore
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @JsonIgnore
    private ScheduledFuture<?> contentUpdateFuture;
    private Iterator<String> iterator;

    public Channel() {
    }
    public Channel(String channelId, String channelDescription) {
        this.channelId = channelId;
        this.channelDescription = channelDescription;
        resetIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Channel channel = (Channel) o;
        return Objects.equals(channelId, channel.channelId) &&
                Objects.equals(channelDescription, channel.channelDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, channelDescription);
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public Map<String, Content> getContents() {
        return ImmutableMap.copyOf(this.contents);
    }

    public void addContent(Content content) {
        this.contents.put(content.getUrl(), content);
        resetIterator();
    }

    public void removeContent(Content content) {
        if(this.contents.containsKey(content.getUrl())) {
            this.contents.remove(content.getUrl());
        }
        resetIterator();
    }

    public void updateContent(String url, Content content) {
        this.contents.put(url, content);
    }

    public void resetIterator() {
        this.iterator = this.contents.keySet().iterator();
    }

    @JsonIgnore
    public Content getNextContent() {
        if (!iterator.hasNext()) {
            resetIterator();
        }
        return this.contents.get(iterator.next());
    }

}
