package io.buedchen.server.api;

import io.buedchen.server.*;
import io.buedchen.server.events.project.*;
import io.swagger.annotations.*;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
@Path("/v1")
@Api(value = "/v1", description = "Providers REST API version 1.0 for Buedchen TV", tags = "Providers")
public class Providers {

    private final EventBusWrapper eventBus;
    private final Projects projects;

    public Providers() {
        ResourcesSingleton singleton = ResourcesSingleton.getInstance();
        this.projects = singleton.getProjects();
        this.eventBus = singleton.getEventBus();
    }

    public Providers(Projects projects, EventBusWrapper eventBus) {
        this.projects = projects;
        this.eventBus = eventBus;
    }

    @GET
    @Path("/providers")
    @ApiOperation(value = "Returns all the projects", response = Project.class, responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProviders() {
        List<Project> projects = new ArrayList<>(this.projects.getProjects().values());

        return Response.ok(projects).build();
    }

    @GET
    @Path("/providers/{projectId}")
    @ApiOperation(value = "Returns the Project object for projectID", response = Project.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 404, message = "Project ID not present")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(
            @ApiParam(value = "Project Id", required = true) @PathParam("projectId") String projectId) {
        if (!this.projects.getProjects().containsKey(projectId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(this.projects.getProject(projectId)).build();
    }

    @POST
    @Path("/providers")
    @ApiOperation(value = "Adds a new project")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Project Already exists")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(Project project) {
        checkNotNull(project);
        try {
            this.projects.addProject(project);
            this.eventBus.post(new ProjectCreated(project));
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).type("text/plain").build();
        }
    }

    @PUT
    @Path("/providers/{projectId}")
    @ApiOperation(value = "Update the project")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 200, message = "Successful")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProject(
            @ApiParam(value = "Project Id which needs to be updated", required = true) @PathParam("projectId")
                    String projectId, Project project) {
        checkNotNull(project);
        if (!this.projects.getProjects().containsKey(projectId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.projects.getProject(projectId).setProjectDescription(project.getProjectDescription());
        this.eventBus.post(new ProjectUpdated(project));
        return Response.ok().build();
    }

    @DELETE
    @Path("/providers/{projectId}")
    @ApiOperation(value = "Remove the Project")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 200, message = "Successful")})
    public Response updateProject(
            @ApiParam(value = "Project Id which needs to be removed", required = true) @PathParam("projectId")
                    String projectId) {
        if (!this.projects.getProjects().containsKey(projectId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.projects.getProjects().remove(projectId);
        this.eventBus.post(new ProjectRemoved(projectId));
        return Response.ok().build();
    }

    @GET
    @Path("/providers/{projectId}/dashboards")
    @ApiOperation(value = "Returns the dashboards of project", response = Dashboard.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 200, message = "Successful")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectDashboards(
            @ApiParam(value = "Project Id whose Dashboards are required", required = true) @PathParam("projectId")
                    String projectId) {
        if (!this.projects.getProjects().containsKey(projectId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(this.projects.getProjectDashboards(projectId)).build();
    }

    @GET
    @Path("/providers/{projectId}/dashboards/{dashboardUrl}")
    @ApiOperation(value = "Returns the dashboard of project with dashboardUrl", response = Dashboard.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Invalid Dashboard Url"),
            @ApiResponse(code = 200, message = "Successful")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectDashboard(
            @ApiParam(value = "Project Id", required = true) @PathParam("projectId") String projectId,
            @ApiParam(value = "Dashboard Url", required = true) @PathParam("dashboardUrl") String dashboardUrl) {
        System.out.println("URL :" + dashboardUrl);
        if(!this.projects.getProjectDashboards(projectId).containsKey(dashboardUrl)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(this.projects.getProjectDashboards(projectId).get(dashboardUrl)).build();
    }

    @POST
    @Path("/providers/{projectId}/dashboards")
    @ApiOperation(value = "Add dashboards to the Project", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 200, message = "Successful")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDashboardToProject(
            @ApiParam(value = "Project Id", required = true) @PathParam("projectId") String projectId,
            Dashboard dashboard) {
        checkNotNull(dashboard);
        if (!this.projects.getProjects().containsKey(projectId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.projects.addDashboardToProject(projectId, dashboard);
        this.eventBus.post(new ProjectDashboardAdded(projectId, dashboard));
        return Response.ok().build();
    }

    @PUT
    @Path("/providers/{projectId}/dashboards/{dashboardUrl}")
    @ApiOperation(value = "Update the dashboard of project")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Invalid Dashboard Id"),
            @ApiResponse(code = 200, message = "Successful")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProjectDashboard(
            @ApiParam(value = "Project Id", required = true) @PathParam("projectId") String projectId,
            @ApiParam(value = "Dashboard Url", required = true) @PathParam("dashboardUrl")String dashboardUrl,
            Dashboard dashboard) {
        checkNotNull(dashboard);
        if(!this.projects.getProjects().containsKey(projectId) || this.projects.getProjectDashboards(projectId).get(dashboardUrl) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.projects.getProjectDashboards(projectId).put(dashboardUrl, dashboard);
        this.eventBus.post(new ProjectDashboardUpdated(projectId, dashboardUrl, dashboard));
        return Response.ok().build();
    }

    @DELETE
    @Path("/providers/{projectId}/dashboards/{dashboardUrl}")
    @ApiOperation(value = "Remove the Dashboard", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Project not found or Invalid Dashboard Id"),
            @ApiResponse(code = 200, message = "Successful")})
    public Response deleteProjectDashboard(
            @ApiParam(value = "Project Id", required = true) @PathParam("projectId") String projectId,
            @ApiParam(value = "Dashboard Url", required = true) @PathParam("dashboardUrl") String dashboardUrl) {
        if (!this.projects.getProjects().containsKey(projectId)
                || this.projects.getProjectDashboards(projectId).get(dashboardUrl) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Dashboard dashboard = this.projects.getProjectDashboards(projectId).get(dashboardUrl);
        this.projects.removeDashboardFromProject(projectId, dashboard);
        this.eventBus.post(new ProjectDashboardRemoved(projectId, dashboard, dashboardUrl));
        return Response.ok().build();
    }
}
