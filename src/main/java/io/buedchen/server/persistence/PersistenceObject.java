package io.buedchen.server.persistence;

import io.buedchen.server.Content;
import io.buedchen.server.Dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistenceObject {
    private Map<String, List<Content>> contents = new HashMap<>();
    private Map<String, String> channels = new HashMap<>();
    private Map<String, String> clients = new HashMap<>();

    private Map<String, String> projects = new HashMap<>();
    private Map<String, List<Dashboard>> dashboards = new HashMap<>();

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

    public Map<String, String> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, String> projects) {
        this.projects = projects;
    }

    public Map<String, List<Dashboard>> getProjectContents() {
        return dashboards;
    }

    public void setProjectContents(Map<String, List<Dashboard>> projectContents) {
        this.dashboards = projectContents;
    }
}
