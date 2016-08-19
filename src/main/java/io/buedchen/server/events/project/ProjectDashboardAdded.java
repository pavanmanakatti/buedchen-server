package io.buedchen.server.events.project;

import io.buedchen.server.Dashboard;

public class ProjectDashboardAdded {

    private final String projectId;
    private final Dashboard dashboard;

    public ProjectDashboardAdded(String projectId, Dashboard dashboard) {
        this.projectId = projectId;
        this.dashboard = dashboard;
    }

    public String getProjectId() {
        return projectId;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

}
