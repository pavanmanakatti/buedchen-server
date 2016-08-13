package io.buedchen.server;

import com.google.common.eventbus.EventBus;

public class EventBusWrapper {

    private final EventBus eventBus;

    public EventBusWrapper(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void post(Object event) {
        eventBus.post(event);
    }

    public void register(Object listener) {
        eventBus.register(listener);
    }
}
