package io.buedchen.server.events.project;

import io.buedchen.server.Project;

public class ProjectCreated {
    private final Project project;

    public ProjectCreated(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
