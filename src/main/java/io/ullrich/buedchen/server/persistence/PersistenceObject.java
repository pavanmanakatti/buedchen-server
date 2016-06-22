package io.ullrich.buedchen.server.persistence;

import io.ullrich.buedchen.server.Content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistenceObject {
    private Map<String, List<Content>> contents = new HashMap<>();
    private Map<String, String> channels = new HashMap<>();
    private Map<String, String> clients = new HashMap<>();

    public PersistenceObject() {
    }

    public Map<String, List<Content>> getContents() {
        return contents;
    }

    public void setContents(Map<String, List<Content>> contents) {
        this.contents = contents;
    }

    public Map<String, String> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, String> channels) {
        this.channels = channels;
    }

    public Map<String, String> getClients() {
        return clients;
    }

    public void setClients(Map<String, String> clients) {
        this.clients = clients;
    }
}
