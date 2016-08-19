package io.buedchen.server.events.client;

import java.util.Objects;

public class ClientAssigned {

    private final String clientId;
    private final String channelId;

    public ClientAssigned(String clientId, String channelId) {
        this.clientId = clientId;
        this.channelId = channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientAssigned that = (ClientAssigned) o;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, channelId);
    }

    public String getClientId() {
        return clientId;
    }

    public String getChannelId() {
        return channelId;
    }

}
