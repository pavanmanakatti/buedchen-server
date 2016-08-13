package io.buedchen.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("channel_id")
    private String channelId;

    public Client(){}

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
