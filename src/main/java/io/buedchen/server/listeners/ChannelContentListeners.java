package io.buedchen.server.listeners;

import com.google.common.eventbus.Subscribe;
import io.buedchen.server.Channels;
import io.buedchen.server.Clients;
import io.buedchen.server.Content;
import io.buedchen.server.EventBusWrapper;
import io.buedchen.server.events.client.UpdateClientContent;
import io.buedchen.server.events.content.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChannelContentListeners {
    private static final Logger logger = LoggerFactory.getLogger(ChannelContentListeners.class);

    private final EventBusWrapper eventBus;
    private final Channels channels;
    private final Clients clients;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);
    private final Map<String, ScheduledFuture> channelFutures = new HashMap<>();

    public ChannelContentListeners(EventBusWrapper eventBus, Channels channels, Clients clients) {
        this.eventBus = eventBus;
        this.channels = channels;
        this.clients = clients;
    }

    @Subscribe
    public void channelContentAdded(ChannelContentAdded channelContentAdded) {
        String channelId = channelContentAdded.getChannelId();
        Content content = channelContentAdded.getContent();
        if (this.channels.getChannelContents(channelId).size() > 1) {
            this.channels.getChannel(channelId).resetIterator();
            this.eventBus.post(new ChannelUpdateCurrentContent(channelId));
        }
    }

    @Subscribe
    public void channelContentRemoved(ChannelContentRemoved channelContentRemoved) {
        String channelId = channelContentRemoved.getChannelId();
        Content content = channelContentRemoved.getContent();
        if (this.channels.getChannelContents(channelId).size() > 1) {
            this.channels.getChannel(channelId).resetIterator();
            this.eventBus.post(new ChannelUpdateCurrentContent(channelId));
        }
    }

    @Subscribe
    public void startContentUpdates(StartContentUpdates startContentUpdates) {
        for (String channelId : this.channels.getChannels().keySet()) {
            if (this.channels.getChannelContents(channelId).size() > 0) {
                this.eventBus.post(new ChannelUpdateCurrentContent(channelId));
            }
        }
    }

    @Subscribe
    public void channelScheduleContentUpdate(ChannelUpdateCurrentContent channelUpdateCurrentContent) {
        String channelId = channelUpdateCurrentContent.getChannelId();
        logger.info("Scheduling content update on {}", channelId);
        if (!this.channels.getChannels().containsKey(channelId) || this.channels.getChannelContents(channelId)
                .isEmpty()) {
            logger.debug("Not scheduling anything - channel does not exist or has no content");
            return;
        }

        Content nextContent = this.channels.getChannel(channelId).getNextContent();

        if (this.channelFutures.containsKey(channelId)) {
            this.channelFutures.get(channelId).cancel(false);
            this.channelFutures.remove(channelId);
        }

        ScheduledFuture future = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                logger.debug("Triggering channel update for {}", channelId);
                eventBus.post(new ChannelUpdateCurrentContent(channelId));
            }
        }, nextContent.getShowtime(), TimeUnit.SECONDS);
        this.channelFutures.put(channelId, future);
        eventBus.post(new ChannelCurrentContentUpdated(channelId, nextContent));
    }

    @Subscribe
    public void channelCurrentContentUpdated(ChannelCurrentContentUpdated channelCurrentContentUpdated) {
        logger.debug("ChannelCurrentContentUpdated");
        String channel = channelCurrentContentUpdated.getClientId();
        Content content = channelCurrentContentUpdated.getContent();
        for (String clientId : clients.getClientIds()) {
            if (channel.equals(clients.getClient(clientId).getChannelId())) {
                eventBus.post(new UpdateClientContent(clientId, content));
            }
        }
    }

}
