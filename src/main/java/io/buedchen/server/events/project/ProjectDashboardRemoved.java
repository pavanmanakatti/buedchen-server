package io.buedchen.server.events.project;

import io.buedchen.server.Dashboard;

public class ProjectDashboardRemoved {
    private final String projectId;
    private final Dashboard dashboard;
    private final String dashboardUrl;

    public ProjectDashboardRemoved(String projectId, Dashboard dashboard, String dashboardUrl) {
        this.projectId = projectId;
        this.dashboard = dashboard;
        this.dashboardUrl = dashboardUrl;
    }

    public String getProjectId() {
        return projectId;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

}
