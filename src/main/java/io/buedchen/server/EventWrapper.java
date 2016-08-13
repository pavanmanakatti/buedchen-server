package io.buedchen.server;

public class EventWrapper {
    private String type;
    private Object event;

    public EventWrapper() {
    }

    public EventWrapper(String type, Object event) {
        this.type = type;
        this.event = event;
    }

    public String getType() {
        return type;
    }

    public Object getEvent() {
        return event;
    }
}
