package io.buedchen.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class JacksonTest {

    @Test
    public void clientTest() throws Exception{
        Client[] clients = new Client[4];
        Client[] result = new Client[4];

        clients[0] = new Client("7eabed44215","channel 1");
        clients[1] = new Client("f031920d575-muenster1","channel 2");
        clients[2] = new Client("e0bfc5a4f83b","channel 3");
        clients[3] = new Client("2f6efec882a7","channel 4");

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(clients);
        result = mapper.readValue(json, Client[].class);

        boolean res = Arrays.equals(clients, result);
        Assert.assertTrue(res);
    }

    @Test
    public void channelTest() throws IOException {

        Channel[] channels = new Channel[4];
        Channel[] result = new Channel[4];

        channels[0] = new Channel("test channel 1", "channel description 1");
        channels[1] = new Channel("test channel 2", "channel description 2");
        channels[2] = new Channel("test channel 3", "channel description 3");
        channels[3] = new Channel("test channel 4", "channel description 4");

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(channels);
        result = mapper.readValue(json, Channel[].class);

        boolean res = Arrays.equals(channels, result);
        Assert.assertTrue(res);
    }

    @Test
    public void contentTest() throws IOException {

        Content[] contents = new Content[4];
        Content[] result = new Content[4];

        contents[0] = new Content("http://www.google.com/1", 30, "content0");
        contents[1] = new Content("http://www.google.com/2", 60, "content1");
        contents[2] = new Content("http://www.google.com/3", 120, "content2");
        contents[3] = new Content("http://www.google.com/4", 60, "content3");

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(contents);
        result = mapper.readValue(json, Content[].class);

        boolean res = Arrays.equals(contents, result);
        Assert.assertTrue(res);
    }

}
