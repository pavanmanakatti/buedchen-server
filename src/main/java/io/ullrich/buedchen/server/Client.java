package io.ullrich.buedchen.server;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String clientId;
    private String channelId;

    public Client(String clientId) {
        this.clientId = clientId;
        logger.info("Added new client {}", this.toString());
    }

    public Client(String clientId, String channelId) {
        this.clientId = clientId;
        this.channelId = channelId;
        logger.info("Added new client {}", this.toString());
    }

    public String getClientId() {
        return clientId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.clientId);
        hash = 29 * hash + Objects.hashCode(this.channelId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (!Objects.equals(this.clientId, other.clientId)) {
            return false;
        }
        if (!Objects.equals(this.channelId, other.channelId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Client{" + "clientId=" + clientId + ", channelId=" + channelId + '}';
    }
}
