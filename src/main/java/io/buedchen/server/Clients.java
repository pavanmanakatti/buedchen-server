package io.buedchen.server;

import io.buedchen.server.exceptions.ClientAlreadyExistsException;
import io.buedchen.server.exceptions.ClientNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Clients {

    private final Map<String, Client> clients = Collections.synchronizedMap(new HashMap<>());

    public Clients() {
    }

    public Client getClient(String clientId) {
        ensureClientExists(clientId);
        return clients.get(clientId);
    }

    public Map<String, Client> getClients() {
        return clients;
    }

    public void addClient(String clientId) throws ClientAlreadyExistsException {
        ensureClientDoesNotExist(clientId);
        clients.put(clientId, new Client(clientId));
    }

    public void addClient(String clientId, String channelId) throws ClientAlreadyExistsException {
        ensureClientDoesNotExist(clientId);
        clients.put(clientId, new Client(clientId, channelId));
    }

    public Set<String> getClientIds() {
        return clients.keySet();
    }

    private void ensureClientDoesNotExist(String clientId) throws ClientAlreadyExistsException {
        if (clients.containsKey(clientId)) {
            throw new ClientAlreadyExistsException("Client does already exist");
        }
    }

    private void ensureClientExists(String clientId) throws ClientNotFoundException {
        if (!clients.containsKey(clientId)) {
            throw new ClientNotFoundException("Client does not exist");
        }
    }
}
