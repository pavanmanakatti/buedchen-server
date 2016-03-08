package io.ullrich.buedchen.server.events.client;

public class ClientAssigned {

    private final String clientId;
    private final String channelId;

    public ClientAssigned(String clientId, String channelId) {
        this.clientId = clientId;
        this.channelId = channelId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getChannelId() {
        return channelId;
    }

}
