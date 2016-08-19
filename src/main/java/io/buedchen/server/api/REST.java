package io.buedchen.server.api;

import io.buedchen.server.*;
import io.swagger.annotations.*;
import io.buedchen.server.*;
import io.buedchen.server.events.channel.ChannelCreated;
import io.buedchen.server.events.channel.ChannelRemoved;
import io.buedchen.server.events.channel.ChannelUpdated;
import io.buedchen.server.events.client.ClientAssigned;
import io.buedchen.server.events.content.ChannelContentAdded;
import io.buedchen.server.events.content.ChannelContentRemoved;
import io.buedchen.server.events.content.ChannelContentUpdated;
import io.buedchen.server.exceptions.ChannelAlreadyExistsException;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
@Path("/v1")
@Api(value = "/v1", description = "REST API version 1.0 for Buedchen TV", tags = "Channels")
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

    public REST(Channels channels, Clients clients, EventBusWrapper eventBus) {
        this.channels = channels;
        this.clients = clients;
        this.eventBus = eventBus;
    }

    @GET
    @Path("/clients")
    @ApiOperation(value = "Return all clients",
            notes = "Returns the client objects which maps client ID to channel IDs", response = Client.class,
            responseContainer = "List")
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
    @ApiOperation(value = "Update the channel ID for a Client",
            notes = "Updates the Client ID if the client is present if not creates the Client with the given attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assignClient(
            @ApiParam(value = "Client Id which needs to be updated/created", required = true) @PathParam("clientId")
                    String clientId, Client client) {
        checkNotNull(client);
        if (this.clients.getClientIds().contains(clientId)) {
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
    @ApiOperation(value = "Returns all the channels", response = Channel.class, responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannels() {
        List<Channel> channels = new ArrayList<>(this.channels.getChannels().values());
        return Response.ok(channels).build();
    }

    @GET
    @Path("/channels/{channelId}")
    @ApiOperation(value = "Returns the Channel object for channelID", response = Channel.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 404, message = "Channel ID not present")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannel(
            @ApiParam(value = "Channel Id", required = true) @PathParam("channelId") String channelId) {
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(this.channels.getChannel(channelId)).build();
    }

    @POST
    @Path("/channels")
    @ApiOperation(value = "Adds a new Channel")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Channel Already exists")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createChannel(Channel channel) {
        checkNotNull(channel);
        try {
            this.channels.addChannel(channel);
            this.eventBus.post(new ChannelCreated(channel));
            return Response.ok().build();
        } catch (ChannelAlreadyExistsException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/channels/{channelId}")
    @ApiOperation(value = "Update the channel")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 200, message = "Successful")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateChannel(
            @ApiParam(value = "Channel Id which needs to be updated", required = true) @PathParam("channelId")
                    String channelId, Channel channel) {
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
    @ApiOperation(value = "Remove the Channel")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 200, message = "Successful")})
    public Response updateChannel(
            @ApiParam(value = "Channel Id which needs to be removed", required = true) @PathParam("channelId")
                    String channelId) {
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.channels.getChannels().remove(channelId);
        this.eventBus.post(new ChannelRemoved(channelId));
        return Response.ok().build();

    }

    @GET
    @Path("/channels/{channelId}/contents")
    @ApiOperation(value = "Returns the contents of channel", response = Content.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 200, message = "Successful")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelContent(
            @ApiParam(value = "Channel Id whose contents are required", required = true) @PathParam("channelId")
                    String channelId) {
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Content> contentsList = new ArrayList<>(this.channels.getChannelContents(channelId).values());
        return Response.ok(contentsList).build();
    }

    @GET
    @Path("/channels/{channelId}/contents/{contentUrl}")
    @ApiOperation(value = "Returns the content of channel with contentUrl", response = Content.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Invalid Content Url"),
            @ApiResponse(code = 200, message = "Successful")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelContent(
            @ApiParam(value = "Channel Id", required = true) @PathParam("channelId") String channelId,
            @ApiParam(value = "Content URL", required = true) @PathParam("contentUrl") String contentUrl) {
        if(!this.channels.getChannelContents(channelId).containsKey(contentUrl)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(this.channels.getChannelContents(channelId).get(contentUrl)).build();
    }

    @POST
    @Path("/channels/{channelId}/contents")
    @ApiOperation(value = "Add contents to the Channel", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 200, message = "Successful")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addContentToChannel(
            @ApiParam(value = "Channel Id", required = true) @PathParam("channelId") String channelId,
            Content content) {
        checkNotNull(content);
        if (!this.channels.getChannels().containsKey(channelId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.channels.addContentToChannel(channelId, content);
        this.eventBus.post(new ChannelContentAdded(channelId, content));
        return Response.ok().build();
    }

    @PUT
    @Path("/channels/{channelId}/contents/{contentUrl}")
    @ApiOperation(value = "Update the content of channel")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Invalid Content URL"),
            @ApiResponse(code = 200, message = "Successful")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateChannelContent(
            @ApiParam(value = "Channel Id", required = true) @PathParam("channelId") String channelId,
            @ApiParam(value = "Content URL", required = true) @PathParam("contentUrl") String contentUrl,
            Content content) {
        checkNotNull(content);
        if(!this.channels.getChannels().containsKey(channelId) || this.channels.getChannelContents(channelId).get(contentUrl) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.channels.getChannelContents(channelId).put(contentUrl, content);
        this.eventBus.post(new ChannelContentUpdated(channelId, contentUrl, content));
        return Response.ok().build();
    }

    @DELETE
    @Path("/channels/{channelId}/contents/{contentUrl}")
    @ApiOperation(value = "Remove the content", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Channel not found or Invalid Content Id"),
            @ApiResponse(code = 200, message = "Successful")})
    public Response deleteChannelContent(
            @ApiParam(value = "Channel Id", required = true) @PathParam("channelId") String channelId,
            @ApiParam(value = "Content URL", required = true) @PathParam("contentUrl") String contentUrl) {
        if (!this.channels.getChannels().containsKey(channelId)
                || this.channels.getChannelContents(channelId).get(contentUrl) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Content content = this.channels.getChannelContents(channelId).get(contentUrl);
        this.channels.removeContentFromChannel(channelId, content);
        this.eventBus.post(new ChannelContentRemoved(channelId, content, contentUrl));
        return Response.ok().build();
    }

    @GET
    @Path("/unassigned")
    @ApiOperation(value = "", notes = "")
    @Produces(MediaType.TEXT_HTML)
    public String getUnassigned() {
        return "<html><head></head>Client not assigned to a channel</html>";
    }
}
