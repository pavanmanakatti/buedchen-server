package io.buedchen.server.Storage;

import io.buedchen.server.Channels;
import io.buedchen.server.Clients;
import io.buedchen.server.Projects;

public interface Storage {
    void persistChannels(Channels channels);

    void persistClients(Clients clients);

    void persistProjects(Projects projects);
}
