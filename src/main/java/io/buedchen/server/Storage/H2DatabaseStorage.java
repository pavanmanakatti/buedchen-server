package io.buedchen.server.Storage;

import io.buedchen.server.*;

import java.util.Map;

public class H2DatabaseStorage implements Storage {

    private DatabaseHelper databaseHelper;

    public H2DatabaseStorage() {
        databaseHelper = new DatabaseHelper();
    }

    public void persistChannels(Channels channels) {

        databaseHelper.statementExecuteUpdate(SqlStatements.DROP_CHANNELS_TABLE);
        databaseHelper.statementExecuteUpdate(SqlStatements.CREATE_CHANNELS_TABLE);

        for (Map.Entry<String, Channel> channelEntry : channels.getChannels().entrySet()) {
            String channelId = channelEntry.getKey();
            Channel channel = channelEntry.getValue();
            String channelDescription = channel.getChannelDescription();

            databaseHelper.statementExecuteUpdate(SqlStatements.INSERT_CHANNEL_VALUES, channelId, channelDescription);
        }

        persistChannelContents(channels);
    }

    private void persistChannelContents(Channels channels) {

        databaseHelper.statementExecuteUpdate(SqlStatements.DROP_CONTENTS_TABLE);
        databaseHelper.statementExecute(SqlStatements.CREATE_CONTENTS_TABLE);

        for (Map.Entry<String, Channel> channelEntry : channels.getChannels().entrySet()) {
            String channelId = channelEntry.getKey();
            Map<String,Content> contents = channelEntry.getValue().getContents();
            for(Map.Entry<String,Content> contentEntry : contents.entrySet()) {
                String url = contentEntry.getValue().getUrl();
                Integer showtime = contentEntry.getValue().getShowtime();
                String title = contentEntry.getValue().getTitle();
                databaseHelper.statementExecuteUpdate(SqlStatements.INSERT_CONTENTS_VALUES, channelId, url, showtime, title);
            }
        }
    }

    public void persistClients(Clients clients) {
        databaseHelper.statementExecuteUpdate(SqlStatements.DROP_CLIENTS_TABLE);

        databaseHelper.statementExecute(SqlStatements.CREATE_CLIENTS_TABLE);

        for (Map.Entry<String, Client> clientEntry : clients.getClients().entrySet()) {
            String clientId = clientEntry.getKey();
            Client client = clientEntry.getValue();
            String channelId = client.getChannelId();

            databaseHelper.statementExecuteUpdate(SqlStatements.INSERT_CLIENTS_VALUES, clientId, channelId);
        }
    }

    public void persistProjects(Projects projects) {

        databaseHelper.statementExecuteUpdate(SqlStatements.DROP_PROJECTS_TABLE);

        databaseHelper.statementExecute(SqlStatements.CREATE_PROJECTS_TABLE);

        for (Map.Entry<String, Project> projectEntry : projects.getProjects().entrySet()) {
            String projectId = projectEntry.getKey();
            Project project = projectEntry.getValue();
            String projectDescription = project.getProjectDescription();

            databaseHelper.statementExecuteUpdate(SqlStatements.INSERT_PROJECT_VALUES, projectId, projectDescription);
        }

        persistProjectDashboards(projects);
    }

    private void persistProjectDashboards(Projects projects) {

        databaseHelper.statementExecuteUpdate(SqlStatements.DROP_DASHBOARDS_TABLE);
        databaseHelper.statementExecute(SqlStatements.CREATE_DASHBOARDS_TABLE);

        for (Map.Entry<String, Project> projectEntry : projects.getProjects().entrySet()) {
            String projectId = projectEntry.getKey();
            Map<String,Dashboard> dashboards = projectEntry.getValue().getDashboards();
            for(Map.Entry<String,Dashboard> dashboardEntry : dashboards.entrySet()) {
                String url = dashboardEntry.getValue().getUrl();
                String title = dashboardEntry.getValue().getTitle();
                String description = dashboardEntry.getValue().getDescription();
                databaseHelper.statementExecuteUpdate(SqlStatements.INSERT_DASHBOARD_VALUES, projectId, url, description, title);
            }
        }
    }
}
