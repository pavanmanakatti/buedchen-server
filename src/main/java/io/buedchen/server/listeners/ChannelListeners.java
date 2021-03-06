package io.buedchen.server.listeners;

import com.google.common.eventbus.Subscribe;
import io.buedchen.server.Channels;
import io.buedchen.server.Clients;
import io.buedchen.server.events.channel.CreateChannel;
import io.buedchen.server.EventBusWrapper;
import io.buedchen.server.events.channel.ChannelCreated;
import io.buedchen.server.events.channel.ChannelRemoved;
import io.buedchen.server.events.channel.ChannelUpdated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelListeners {

    private static final Logger logger = LoggerFactory.getLogger(ChannelListeners.class);

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
