package io.buedchen.server.events.project;

import io.buedchen.server.Project;

public class ProjectUpdated {
    public final Project project;

    public ProjectUpdated(Project project) {
        this.project = project;
    }

}
