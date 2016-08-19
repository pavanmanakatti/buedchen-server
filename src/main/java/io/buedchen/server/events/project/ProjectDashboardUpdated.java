package io.buedchen.server.events.project;

import io.buedchen.server.Dashboard;

public class ProjectDashboardUpdated {
    private final String projectId;
    private final String dashboardUrl;
    private final Dashboard dashboard;

    public ProjectDashboardUpdated(String projectId, String dashboardUrl, Dashboard dashboard) {
        this.projectId = projectId;
        this.dashboardUrl = dashboardUrl;
        this.dashboard = dashboard;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

}
