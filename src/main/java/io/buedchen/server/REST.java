package io.buedchen.server;

import io.buedchen.server.events.content.ChannelContentRemoved;
import io.buedchen.server.events.content.ChannelContentUpdated;
import io.buedchen.server.exceptions.ChannelAlreadyExistsException;
import io.buedchen.server.events.channel.ChannelCreated;
import io.buedchen.server.events.channel.ChannelRemoved;
import io.buedchen.server.events.channel.ChannelUpdated;
import io.buedchen.server.events.client.ClientAssigned;
import io.buedchen.server.events.content.ChannelContentAdded;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
@Path("/v1")
public class REST {

    private final EventBusWrapper eventBus;
    private final Clients clients;
    private final Channels channels;

    public REST() {
        ResourcesSingleton singleton = ResourcesSingleton.getInstance();
        this.channels = singleton.getChannels();
        this.clients = singleton.getClients();
        this.eventBus = singleton.getEventBus();

    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientIds() {
        List<Client> clientList = new ArrayList<>();
        for (String id : clients.getClientIds()) {
            clientList.add(clients.getClient(id));
        }
        return Response.ok(clientList).build();
    }

    @PUT
    @Path("/clients/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assignClient(@PathParam("clientId") String clientId, Client client) {
        checkNotNull(client);
        if(this.clients.getClientIds().contains(clientId)) {
            this.clients.getClient(clientId).setChannelId(client.getChannelId());
            this.eventBus.post(new ClientAssigned(clientId, client.getChannelId()));
            return Response.ok().build();
        } else {
            this.clients.addClient(clientId, client.getChannelId());
            this.eventBus.post(new ClientAssigned(clientId, client.getChannelId()));
            return Response.ok().build();
        }

    }

    @GET
    @Path("/channels")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannels() {

        List<Channel> channels = new ArrayList<>(this.channels.getChannels().values());
        return Response.ok(channels).build();
    }

    @GET
    @Path("/channels/{channelId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannel(@PathParam("channelId") String channelId) {
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(this.channels.getChannel(channelId)).build();
    }

    @POST
    @Path("/channels")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createChannel(Channel channel) {
        checkNotNull(channel);
        try {
            this.channels.addChannel(channel);
            this.eventBus.post(new ChannelCreated(channel));
            return Response.ok().build();
        } catch(ChannelAlreadyExistsException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/channels/{channelId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateChannel(@PathParam("channelId") String channelId, Channel channel) {
        checkNotNull(channel);
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.channels.getChannel(channelId).setChannelDescription(channel.getChannelDescription());
        this.eventBus.post(new ChannelUpdated(channel));
        return Response.ok().build();
    }

    @DELETE
    @Path("/channels/{channelId}")
    public Response updateChannel(@PathParam("channelId") String channelId) {
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.channels.getChannels().remove(channelId);
        this.eventBus.post(new ChannelRemoved(channelId));
        return Response.ok().build();
    }

    @GET
    @Path("/channels/{channelId}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelContent(@PathParam("channelId") String channelId) {
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(this.channels.getChannelContents(channelId)).build();
    }

    @GET
    @Path("/channels/{channelId}/contents/{contentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelContent(@PathParam("channelId") String channelId, @PathParam("contentId") Integer contentId) {
        return Response.ok(this.channels.getChannelContents(channelId).get(contentId)).build();
    }

    @POST
    @Path("/channels/{channelId}/contents")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addContentToChannel(@PathParam("channelId") String channelId, Content content) {
        checkNotNull(content);
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.channels.addContentToChannel(channelId, content);
        this.eventBus.post(new ChannelContentAdded(channelId, content));
        return Response.ok().build();
    }

    @PUT
    @Path("/channels/{channelId}/contents/{contentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateChannelContent(@PathParam("channelId") String channelId,
            @PathParam("contentId") Integer contentId, Content content) {
        checkNotNull(content);
        if (contentId < 0 || contentId > this.channels.getChannelContents(channelId).size()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.channels.getChannelContents(channelId).set(contentId, content);
        this.eventBus.post(new ChannelContentUpdated(channelId, contentId, content));
        return Response.ok().build();
    }

    @DELETE
    @Path("/channels/{channelId}/contents/{contentId}")
    public Response deleteChannelContent(@PathParam("channelId") String channelId,
            @PathParam("contentId") Integer contentId) {
        if (!this.channels.getChannels().containsKey(channelId)
                || this.channels.getChannelContents(channelId).get(contentId) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Content content = this.channels.getChannelContents(channelId).get(contentId);
        this.channels.removeContentFromChannel(channelId, content);
        this.eventBus.post(new ChannelContentRemoved(channelId, content));
        return Response.ok().build();
    }

    @GET
    @Path("/unassigned")
    @Produces(MediaType.TEXT_HTML)
    public String getUnassigned() {
        return "<html><head></head>Client not assigned to a channel</html>";
    }
}
