package io.buedchen.server;

import io.buedchen.server.exceptions.ProjectNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Projects {

    private final Map<String, Project> projects = Collections.synchronizedMap(new HashMap<>());

    public Projects() {

    }

    public Map<String, Project> getProjects() {
        return projects;
    }

    public Project getProject(String projectId) {
        return projects.get(projectId);
    }

    public void addProject(Project project) {
        projects.put(project.getProjectId(), project);
    }

    public void addDashboardToProject(String projectId, Dashboard dashboard) {
        ensureProjectExists(projectId);
        if (!getProject(projectId).getDashboards().containsValue(dashboard)) {
            getProject(projectId).addDashboard(dashboard);
        }
    }

    public Map<String, Dashboard> getProjectDashboards(String projectId) {
        ensureProjectExists(projectId);
        return this.projects.get(projectId).getDashboards();
    }

    public void removeDashboardFromProject(String projectId, Dashboard dashboard) {
        ensureProjectExists(projectId);
        if (getProjectDashboards(projectId).containsValue(dashboard)) {
            getProjectDashboards(projectId).remove(dashboard.getUrl());
        }
    }

    private void ensureProjectExists(String projectId) throws ProjectNotFoundException {
        if (!projects.containsKey(projectId)) {
            throw new ProjectNotFoundException("Project does not exist");
        }
    }
}
