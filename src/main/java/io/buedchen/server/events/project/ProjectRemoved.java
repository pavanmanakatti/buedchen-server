package io.buedchen.server.events.project;

public class ProjectRemoved {

    private final String projectId;

    public ProjectRemoved(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }
}
