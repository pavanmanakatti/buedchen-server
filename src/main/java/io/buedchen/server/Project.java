package io.buedchen.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("project_description")
    private String projectDescription;

    @JsonIgnore
    private final Map<String,Dashboard> dashboards = Collections.synchronizedMap(new HashMap<String, Dashboard>());

    public Project() {

    }

    public Project(String projectId, String projectDescription) {
        this.projectId = projectId;
        this.projectDescription = projectDescription;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Map<String,Dashboard> getDashboards() {
        return ImmutableMap.copyOf(this.dashboards);
    }

    public void addDashboard(Dashboard dashboard) {
        this.dashboards.put(dashboard.getUrl(),dashboard);
    }

    public void setDashboardStatus(String url, String status) {
        this.dashboards.get(url).setStatus(status);
    }

    public void removeDashboard(String url) {
        this.dashboards.remove(url);
    }

    public void updateDashboard(String url, Dashboard dashboard) {
        this.dashboards.put(url,dashboard);
    }
}
