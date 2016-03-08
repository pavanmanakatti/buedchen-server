package io.ullrich.buedchen.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Channel {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String channelId;
    private String channelDescription;
    private List<Content> contents = Collections.synchronizedList(new ArrayList<Content>());
    private AtomicInteger contentPtr = new AtomicInteger();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> contentUpdateFuture;

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
