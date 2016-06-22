package io.ullrich.buedchen.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private List<Content> contents = Collections.synchronizedList(new ArrayList<Content>());
    @JsonIgnore
    private AtomicInteger contentPtr = new AtomicInteger();

    @JsonIgnore
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @JsonIgnore
    private ScheduledFuture<?> contentUpdateFuture;

    public Channel() {
    }

    public Channel(String channelId, String channelDescription) {
        this.channelId = channelId;
        this.channelDescription = channelDescription;
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

    protected List<Content> getContents() {
        return contents;
    }

    protected void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public Integer getContentPtr() {
        return contentPtr.get();
    }

    public void setContentPtr(Integer contentPtr) {
        this.contentPtr.set(contentPtr);
    }

}
