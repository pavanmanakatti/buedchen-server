package io.buedchen.server;


public class ResourcesSingleton {

    private  Channels channels;
    private  Clients clients;
    private EventBusWrapper eventBus;

    private ResourcesSingleton() {
    }

    private static class ResourcesSingletonHolder {
        private static final ResourcesSingleton INSTANCE = new ResourcesSingleton();
    }

    public static ResourcesSingleton getInstance() {
        return ResourcesSingletonHolder.INSTANCE;
    }

    public void setChannels(Channels channels) {
        this.channels = channels;
    }

    public void setClients(Clients clients) {
        this.clients = clients;
    }

    public void setEventBus(EventBusWrapper eventBus) {
        this.eventBus = eventBus;
    }

    public Channels getChannels() {
        if(channels.equals(null)) {
            throw new RuntimeException("Channels is null");
        }
        return channels;
    }

    public Clients getClients() {
        if(clients.equals(null)) {
            throw new RuntimeException("Clients is null");
        }
        return clients;
    }

    public EventBusWrapper getEventBus() {
        if(eventBus.equals(null)) {
            throw new RuntimeException("Eventbus is null");
        }
        return eventBus;
    }


}