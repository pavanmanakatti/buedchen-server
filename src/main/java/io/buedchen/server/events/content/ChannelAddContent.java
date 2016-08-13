package io.buedchen.server.events.content;

public class ChannelAddContent {

    private final String channelId;
    private final String url;
    private final Integer showtime;
    private final String header;
    private final String description;

    public ChannelAddContent(String channelId, String url, Integer showtime, String header, String description) {
        this.channelId = channelId;
        this.url = url;
        this.showtime = showtime;
        this.header = header;
        this.description = description;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getUrl() {
        return url;
    }

    public Integer getShowtime() {
        return showtime;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

}
