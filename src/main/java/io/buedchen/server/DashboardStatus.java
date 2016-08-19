package io.buedchen.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

public class DashboardStatus {

    private static final Logger logger = LoggerFactory.getLogger(DashboardStatus.class);
    private final Projects projects;

    public DashboardStatus() {
        ResourcesSingleton singleton = ResourcesSingleton.getInstance();
        this.projects = singleton.getProjects();
    }

    public void updateStatus() {
        for (Map.Entry<String, Project> projectEntry : projects.getProjects().entrySet()) {
            Project project = projectEntry.getValue();
            for(Dashboard dashboard : project.getDashboards().values()) {
                String status = getDashboardStatus(dashboard.getUrl());
                project.setDashboardStatus(dashboard.getUrl(), status);
            }
        }
    }

    private String getDashboardStatus(String url) {
        try {
            // create HTTP Client
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create new getRequest with below mentioned URL
            HttpGet getRequest = new HttpGet(url);

            // Execute your request and catch response
            HttpResponse response = httpClient.execute(getRequest);

            // Check for HTTP response code: 200 = success
            if (response.getStatusLine().getStatusCode() / 100 == 2) {
                return "Available";
            } else {
                return "Not Available";
            }

        } catch (ClientProtocolException e) {
            logger.error("Error : ", e);
        } catch (UnknownHostException e) {
            logger.error("Error :", e);
        } catch (IOException e) {
            logger.error("Error : ", e);
        }
        return "Not Available";
    }

}
