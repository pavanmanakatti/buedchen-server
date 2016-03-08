package io.ullrich.buedchen.server.listeners;

import com.google.common.eventbus.Subscribe;
import io.ullrich.buedchen.server.Channels;
import io.ullrich.buedchen.server.Clients;
import io.ullrich.buedchen.server.Content;
import io.ullrich.buedchen.server.EventBusWrapper;
import io.ullrich.buedchen.server.events.client.UpdateClientContent;
import io.ullrich.buedchen.server.events.content.ChannelContentAdded;
import io.ullrich.buedchen.server.events.content.ChannelContentRemoved;
import io.ullrich.buedchen.server.events.content.ChannelCurrentContentUpdated;
import io.ullrich.buedchen.server.events.content.ChannelUpdateCurrentContent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChannelContentListeners {

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
            this.channels.getChannel(channelId).setContentPtr(0);
            this.eventBus.post(new ChannelUpdateCurrentContent(channelId));
        }
    }

    @Subscribe
    public void channelContentRemoved(ChannelContentRemoved channelContentRemoved) {
        String channelId = channelContentRemoved.getChannelId();
        Content content = channelContentRemoved.getContent();
        if (this.channels.getChannelContents(channelId).size() > 1) {
            this.channels.getChannel(channelId).setContentPtr(0);
            this.eventBus.post(new ChannelUpdateCurrentContent(channelId));
        }
    }

    @Subscribe
    public void channelScheduleContentUpdate(ChannelUpdateCurrentContent channelUpdateCurrentContent) {
        String channelId = channelUpdateCurrentContent.getChannelId();

        if (this.channels.getChannels().containsKey(channelId) && !this.channels.getChannelContents(channelId).isEmpty()) {
            Integer currentPtr = this.channels.getChannel(channelId).getContentPtr();
            Integer numberOfContents = this.channels.getChannelContents(channelId).size();
            Integer nextPtr = (currentPtr + 1) % numberOfContents;
            Content nextContent = this.channels.getChannelContents(channelId).get(nextPtr);

            this.channels.getChannel(channelId).setContentPtr(nextPtr);
            ScheduledFuture future = scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    eventBus.post(new ChannelUpdateCurrentContent(channelId));
                }
            }, nextContent.getShowtime(), TimeUnit.SECONDS);
            this.channelFutures.put(channelId, future);
            eventBus.post(new ChannelCurrentContentUpdated(channelId, nextContent));

        }
    }

    @Subscribe
    public void channelCurrentContentUpdated(ChannelCurrentContentUpdated channelCurrentContentUpdated) {
        String channel = channelCurrentContentUpdated.getClientId();
        Content content = channelCurrentContentUpdated.getContent();
        for (String clientId : clients.getClientIds()) {
            if (channel.equals(clients.getClient(clientId).getClientId())) {
                eventBus.post(new UpdateClientContent(clientId, content));
            }
        }
    }

}
