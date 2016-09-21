package io.buedchen.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.buedchen.server.api.REST;
import io.buedchen.server.events.client.ClientAssigned;
import io.buedchen.server.events.content.ChannelContentRemoved;
import io.buedchen.server.events.content.ChannelContentUpdated;
import io.buedchen.server.events.channel.ChannelCreated;
import io.buedchen.server.events.channel.ChannelRemoved;
import io.buedchen.server.events.channel.ChannelUpdated;
import io.buedchen.server.events.content.ChannelContentAdded;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import java.net.URLEncoder;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RESTTest extends JerseyTest{

    private Clients clients;

    private EventBusWrapper eventBus;

    private Channels channels;

    private static REST rest;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected Application configure(){

        clients = Mockito.mock(Clients.class);
        channels = Mockito.mock(Channels.class);
        eventBus = Mockito.mock(EventBusWrapper.class);
        return new ResourceConfig()
                .register(new REST(channels, clients, eventBus));

    }

    @Test
    public void getClientIdsTest() throws Exception {

        Set<String> set = new HashSet<>();
        set.add("d95c85835d54");
        when(clients.getClientIds()).thenReturn(set);

        Client client = new Client("d95c85835d54", "testChannel");
        when(clients.getClient("d95c85835d54")).thenReturn(client);

        Response get = target("v1/clients").request().get();

        assertThat(get.getStatus(), is(200));

        String jsonStr = get.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        Client[] clientObj = mapper.readValue(jsonStr, Client[].class);

        assertThat(clientObj[0].getClientId(), is("d95c85835d54"));
        assertThat(clientObj[0].getChannelId(), is("testChannel"));

    }

    @Test
    public void assignClientTest() throws Exception {

        Set<String> set = new HashSet<>();
        set.add("d95c85835d54");
        when(clients.getClientIds()).thenReturn(set);

        Client client = new Client("d95c85835d54", "testChannel");
        when(clients.getClient("d95c85835d54")).thenReturn(client);

        Client clientUpdate = new Client("d95c85835d54","testChannel-updated");
        String jsonString = mapper.writeValueAsString(clientUpdate);

        Response get = target("v1/clients/d95c85835d54").request().put(Entity.entity(jsonString,MediaType.APPLICATION_JSON));

        assertThat(get.getStatus(), is(200));

        verify(eventBus, times(1)).post(new ClientAssigned(clientUpdate.getClientId(),clientUpdate.getChannelId()));
    }

    @Test
    public void getChannelsTest() throws Exception {

        Channel channel = new Channel("testChannel","testChannel Description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);
        when(channels.getChannels()).thenReturn(channelMap);

        Response get = target("v1/channels").request().get();

        assertThat(get.getStatus(), is(200));

        String jsonStr = get.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        Channel[] channelObj = mapper.readValue(jsonStr, Channel[].class);

        assertThat(channelObj[0].getChannelId(), is("testChannel"));
        assertThat(channelObj[0].getChannelDescription(), is("testChannel Description"));

    }

    @Test
    public void getChannelTest() throws Exception {

        Channel channel = new Channel("testChannel","testChannel description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);

        when(channels.getChannels()).thenReturn(channelMap);
        when(channels.getChannel("testChannel")).thenReturn(channel);

        Response get = target("v1/channels/testChannel").request().get();

        assertThat(get.getStatus(), is(200));

        String jsonStr = get.readEntity(String.class);

        Channel channelObj = mapper.readValue(jsonStr, Channel.class);

        assertThat(channelObj.getChannelId(), is("testChannel"));
        assertThat(channelObj.getChannelDescription(), is("testChannel description"));

    }

    @Test
    public void createChannel() throws Exception {

        Channel channel = new Channel("testChannel","testChannel description");
        String jsonString = mapper.writeValueAsString(channel);

        Response get = target("v1/channels/").request().post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
        assertThat(get.getStatus(), is(200));

        verify(eventBus,times(1)).post(new ChannelCreated(channel));
    }

    @Test
    public void updateChannelDeleteTest() throws Exception {
        Channel channel = new Channel("testChannel","testChannel description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);

        when(channels.getChannels()).thenReturn(channelMap);

        Response get = target("v1/channels/testChannel").request().delete();
        assertThat(get.getStatus(), is(200));

        verify(eventBus,times(1)).post(new ChannelRemoved("testChannel"));
    }

    @Test
    public void updateChannelPutTest() throws Exception {

        Channel channel = new Channel("testChannel","testChannel description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);

        when(channels.getChannels()).thenReturn(channelMap);
        when(channels.getChannel("testChannel")).thenReturn(channel);

        Channel channelUpdate = new Channel("testChannel","testChannel description - updated");
        String jsonString = mapper.writeValueAsString(channelUpdate);

        Response get = target("v1/channels/testChannel").request().put(Entity.entity(jsonString,MediaType.APPLICATION_JSON));

        assertThat(get.getStatus(), is(200));

        verify(eventBus,times(1)).post(new ChannelUpdated(channelUpdate));

    }

    @Test
    public void getChannelContentTest() throws Exception {
        Channel channel = new Channel("testChannel","testChannel description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);

        when(channels.getChannels()).thenReturn(channelMap);

        Map<String, Content> contents = new HashMap<>();
        Content content = new Content("https://www.google.com",120, "Google");
        contents.put(content.getUrl(),content);

        when(channels.getChannelContents("testChannel")).thenReturn(contents);

        Response get = target("v1/channels/testChannel/contents").request().get();
        assertThat(get.getStatus(), is(200));

        String jsonStr = get.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        Content[] contentObj = mapper.readValue(jsonStr, Content[].class);

        assertThat(contentObj[0].getUrl(), is("https://www.google.com"));
        assertThat(contentObj[0].getShowtime(), is(120));


    }

    @Test
    public void getChannelContentWithUrlTest() throws Exception {

        Map<String, Content> contents = new HashMap<>();
        Content content = new Content("https://www.google.com",120, "Google");
        contents.put(content.getUrl(),content);

        when(channels.getChannelContents("testChannel")).thenReturn(contents);

        String encodedUrl = URLEncoder.encode("https://www.google.com", "UTF-8");
        Response get = target("v1/channels/testChannel/contents/"+encodedUrl).request().get();
        assertThat(get.getStatus(), is(200));

        String jsonStr = get.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();

        Content contentObj = mapper.readValue(jsonStr, Content.class);

        assertThat(contentObj.getUrl(), is("https://www.google.com"));
        assertThat(contentObj.getShowtime(), is(120));

    }

    @Test
    public void addContentToChannelTest() throws Exception {

        Channel channel = new Channel("testChannel","testChannel description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);

        when(channels.getChannels()).thenReturn(channelMap);

        Content content = new Content("http://www.google.com", 30, "Google");
        String jsonString = mapper.writeValueAsString(content);

        Response get = target("v1/channels/testChannel/contents").request().post(Entity.entity(jsonString,MediaType.APPLICATION_JSON));
        assertThat(get.getStatus(), is(200));

        verify(eventBus,times(1)).post(new ChannelContentAdded("testChannel", content));

    }

    @Test
    public void updateChannelContentTest() throws Exception {

        Channel channel = new Channel("testChannel","Test Channel Description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);

        Map<String, Content> contents = new HashMap<>();
        Content content = new Content("https://www.google.com",120, "Google");
        contents.put(content.getUrl(),content);

        when(channels.getChannelContents("testChannel")).thenReturn(contents);
        when(channels.getChannels()).thenReturn(channelMap);

        Content contentUpdate = new Content("https://www.google.com", 30, "Google updated");
        String jsonString = mapper.writeValueAsString(contentUpdate);
        String encodedUrl = URLEncoder.encode("https://www.google.com", "UTF-8");
        Response get = target("v1/channels/testChannel/contents/"+encodedUrl).request().put(Entity.entity(jsonString,MediaType.APPLICATION_JSON));
        assertThat(get.getStatus(), is(200));

        verify(eventBus,times(1)).post(new ChannelContentUpdated("testChannel",contentUpdate.getUrl(),contentUpdate));

    }

    @Test
    public void deleteChannelContentTest() throws Exception {
        Channel channel = new Channel("testChannel","Test Channel Description");
        Map<String, Channel> channelMap = new HashMap<String, Channel>();
        channelMap.put("testChannel", channel);

        when(channels.getChannels()).thenReturn(channelMap);

        Map<String, Content> contents = new HashMap<>();
        Content content = new Content("https://www.google.com",120, "Google");
        contents.put(content.getUrl(),content);

        when(channels.getChannelContents("testChannel")).thenReturn(contents);
        String encodedUrl = URLEncoder.encode("https://www.google.com", "UTF-8");
        Response get = target("v1/channels/testChannel/contents/"+encodedUrl).request().delete();

        assertThat(get.getStatus(), is(200));

        verify(eventBus,times(1)).post(new ChannelContentRemoved("testChannel",content, content.getUrl()));
    }

    @Test
    public void getUnassignedTest() {

        String expected = "<html><head></head>Client not assigned to a channel</html>";
        String response = target("v1/unassigned").request().get(String.class);

        assertThat(expected, is(response));

    }
}
