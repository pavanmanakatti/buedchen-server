package io.ullrich.buedchen.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import io.ullrich.buedchen.server.events.channel.ChannelCreated;
import io.ullrich.buedchen.server.events.channel.ChannelRemoved;
import io.ullrich.buedchen.server.events.channel.ChannelUpdated;
import io.ullrich.buedchen.server.events.client.ClientAssigned;
import io.ullrich.buedchen.server.events.content.ChannelContentAdded;
import io.ullrich.buedchen.server.events.content.ChannelContentRemoved;
import io.ullrich.buedchen.server.events.content.ChannelContentUpdated;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/v1")
public class REST {

    private final EventBusWrapper eventBus;
    private final Clients clients;
    private final Channels channels;

    public REST() {
        this.eventBus = new EventBusWrapper(new EventBus());
        this.clients = null;
        this.channels = null;
    }

    public REST(EventBusWrapper eventBus, Clients clients, Channels channels) {
        this.eventBus = eventBus;
        this.clients = clients;
        this.channels = channels;
    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Client> getClientIds() {
        List<Client> clientList = new ArrayList<>();
        for (String id : clients.getClientIds()) {
            clientList.add(clients.getClient(id));
        }
        return clientList;
    }

    @PUT
    @Path("/clients/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assignClient(@PathParam("clientId") String clientId, Client client) {
        checkNotNull(client);
        this.clients.addClient(clientId, client.getChannelId());
        this.eventBus.post(new ClientAssigned(clientId, client.getChannelId()));
        return Response.ok().build();
    }

    @GET
    @Path("/channels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Channel> getChannels() {
        return new ArrayList<Channel>(this.channels.getChannels().values());
    }

    @POST
    @Path("/channels/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createChannel(Channel channel) {
        checkNotNull(channel);
        this.channels.addChannel(channel);
        this.eventBus.post(new ChannelCreated(channel));
        return Response.ok().build();
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
    public Content getChannelContent(@PathParam("channelId") String channelId, @PathParam("contentId") Integer contentId) {
        return this.channels.getChannelContents(channelId).get(contentId);
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
                || !this.channels.getChannelContents(channelId).contains(contentId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Content content = this.channels.getChannelContents(channelId).get(contentId);
        this.channels.removeContentFromChannel(channelId, content);
        this.eventBus.post(new ChannelContentRemoved(channelId, content));
        return Response.ok().build();
    }

    @GET
    @Path("unassigned")
    @Produces(MediaType.TEXT_HTML)
    public String getUnassigned() {
        return "<html><head></head>Client not assigned to a channel</html>";
    }
}
