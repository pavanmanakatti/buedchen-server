package io.buedchen.server;

import com.google.common.eventbus.EventBus;

public class ResourcesSingleton {

    private Channels channels = new Channels();
    private Clients clients = new Clients();
    private Projects projects = new Projects();
    private EventBusWrapper eventBus = new EventBusWrapper(new EventBus());

    private ResourcesSingleton() {

    }

    public static ResourcesSingleton getInstance() {
        return ResourcesSingletonHolder.INSTANCE;
    }

    public Channels getChannels() {
        return channels;
    }

    public Clients getClients() {
        return clients;
    }

    public Projects getProjects() {
        return projects;
    }

    public EventBusWrapper getEventBus() {
        return eventBus;
    }

    private static class ResourcesSingletonHolder {
        private static final ResourcesSingleton INSTANCE = new ResourcesSingleton();
    }
}
