package io.ullrich.buedchen.server.listeners;

import com.google.common.eventbus.Subscribe;
import io.ullrich.buedchen.server.Channels;
import io.ullrich.buedchen.server.Clients;
import io.ullrich.buedchen.server.EventBusWrapper;
import io.ullrich.buedchen.server.events.channel.ChannelCreated;
import io.ullrich.buedchen.server.events.channel.ChannelRemoved;
import io.ullrich.buedchen.server.events.channel.ChannelUpdated;
import io.ullrich.buedchen.server.events.channel.CreateChannel;

public class ChannelListeners {

    private final EventBusWrapper eventBus;
    private final Channels channels;
    private final Clients clients;

    public ChannelListeners(EventBusWrapper eventBus, Channels channels, Clients clients) {
        this.eventBus = eventBus;
        this.channels = channels;
        this.clients = clients;
    }

    @Subscribe
    public void createChannel(CreateChannel createChannel) {

    }

    @Subscribe
    public void channelCreated(ChannelCreated channelCreated) {

    }

    @Subscribe
    public void channelRemoved(ChannelRemoved channelRemoved) {

    }

    @Subscribe
    public void channelUpdated(ChannelUpdated channelUpdated) {

    }

}
