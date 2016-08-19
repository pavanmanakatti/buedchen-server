package io.buedchen.server.listeners;

import com.google.common.eventbus.Subscribe;
import io.buedchen.server.*;
import io.buedchen.server.events.channel.ChannelCreated;
import io.buedchen.server.events.channel.ChannelRemoved;
import io.buedchen.server.events.channel.ChannelUpdated;
import io.buedchen.server.events.client.ClientAssigned;
import io.buedchen.server.events.content.ChannelContentAdded;
import io.buedchen.server.events.content.ChannelContentRemoved;
import io.buedchen.server.events.content.ChannelContentUpdated;
import io.buedchen.server.events.project.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static io.buedchen.server.Storage.SqlStatements.*;

public class PersistenceDatabaseListeners {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceDatabaseListeners.class);

    @Subscribe
    public void channelContentAdded(ChannelContentAdded channelContentAdded) {

        Content content = channelContentAdded.getContent();
        String channelId = channelContentAdded.getChannelId();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        databaseHelper.statementExecuteUpdate(INSERT_CONTENTS_VALUES, channelId, content.getUrl(),
                content.getShowtime(), content.getTitle());
        databaseHelper.closeConnection();

    }

    @Subscribe
    public void channelContentRemoved(ChannelContentRemoved channelContentRemoved) {

        String channelId = channelContentRemoved.getChannelId();
        String contentUrl = channelContentRemoved.getContentUrl();
        DatabaseHelper databaseHelper = new DatabaseHelper();

        databaseHelper.statementExecuteUpdate(DELETE_FROM_CONTENTS, channelId, contentUrl);
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void assignClient(ClientAssigned clientAssigned) {

        String clientId = clientAssigned.getClientId();
        String channelId = clientAssigned.getChannelId();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        ResultSet rs = databaseHelper.statementExecuteQuery(CHECK_IF_CLIENT_PRESENT, clientId);
        try {
            if (rs.absolute(1)) {
                databaseHelper.statementExecuteUpdate(UPDATE_CHANNEL_FOR_CLIENT, channelId, clientId);
            } else {
                databaseHelper.statementExecuteUpdate(CREATE_CLIENT, clientId, channelId);
            }
        } catch (SQLException e) {
            logger.error("Error : ", e);
        }
        databaseHelper.closeConnection();

    }

    @Subscribe
    public void channelCreated(ChannelCreated channelCreated) {

        Channel channel = channelCreated.getChannel();
        DatabaseHelper databaseHelper = new DatabaseHelper();

        databaseHelper.statementExecuteUpdate(INSERT_CHANNEL_VALUES, channel.getChannelId(),
                channel.getChannelDescription());
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void channelRemoved(ChannelRemoved channelRemoved) {

        String channelId = channelRemoved.getChannelId();
        DatabaseHelper databaseHelper = new DatabaseHelper();

        databaseHelper.statementExecuteUpdate(DELETE_CHANNEL, channelId);
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void channelUpdated(ChannelUpdated channelUpdated) {

        Channel channel = channelUpdated.getChannel();
        DatabaseHelper databaseHelper = new DatabaseHelper();

        databaseHelper.statementExecuteUpdate(UPDATE_CHANNEL, channel.getChannelDescription(), channel.getChannelId());
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void channelContentUpdated(ChannelContentUpdated channelContentUpdated) {

        String channelId = channelContentUpdated.getChannelId();
        String contentUrl = channelContentUpdated.getContentUrl();
        Content content = channelContentUpdated.getContent();
        DatabaseHelper databaseHelper = new DatabaseHelper();
        databaseHelper.statementExecuteUpdate(CHANNEL_CONTENT_UPDATED, content.getShowtime(),
                channelId, contentUrl);
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void projectCreated(ProjectCreated projectCreated) {

        Project project = projectCreated.getProject();
        DatabaseHelper databaseHelper = new DatabaseHelper();
        databaseHelper.statementExecuteUpdate(CREATE_PROJECT, project.getProjectId(), project.getProjectDescription());
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void projectUpdated(ProjectUpdated projectUpdated) {

        Project project = projectUpdated.project;
        DatabaseHelper databaseHelper = new DatabaseHelper();
        databaseHelper.statementExecuteUpdate(UPDATE_PROJECT, project.getProjectDescription(), project.getProjectId());
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void projectRemoved(ProjectRemoved projectRemoved) {

        String projectId = projectRemoved.getProjectId();
        DatabaseHelper databaseHelper = new DatabaseHelper();
        databaseHelper.statementExecuteUpdate(REMOVE_PROJECT, projectId);
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void projectDashboardAdded(ProjectDashboardAdded projectDashboardAdded) {

        Dashboard dashboard = projectDashboardAdded.getDashboard();
        String projectId = projectDashboardAdded.getProjectId();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        databaseHelper.statementExecuteUpdate(INSERT_DASHBOARD_VALUES, projectId, dashboard.getUrl(),
                dashboard.getDescription(), dashboard.getTitle());
        databaseHelper.closeConnection();
    }

    @Subscribe
    public void projectDashboardUpdated(ProjectDashboardUpdated projectDashboardUpdated) {

        String projectId = projectDashboardUpdated.getProjectId();
        String url = projectDashboardUpdated.getDashboardUrl();
        Dashboard dashboard = projectDashboardUpdated.getDashboard();
        DatabaseHelper databaseHelper = new DatabaseHelper();
        databaseHelper.statementExecuteUpdate(PROJECT_DASHBOARD_UPDATED, dashboard.getDescription(), dashboard.getTitle(), projectId,
                dashboard.getUrl());
        databaseHelper.closeConnection();

    }

    @Subscribe
    public void projectDashboardRemoved(ProjectDashboardRemoved projectDashboardRemoved) {

        String projectId = projectDashboardRemoved.getProjectId();
        String dashboardUrl = projectDashboardRemoved.getDashboardUrl();
        DatabaseHelper databaseHelper = new DatabaseHelper();
        databaseHelper.statementExecuteUpdate(PROJECT_DASHBOARD_REMOVED, projectId, dashboardUrl);
        databaseHelper.closeConnection();
    }
}
