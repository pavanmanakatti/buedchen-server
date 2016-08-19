package io.buedchen.server;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Content {

    private String url;
    private Integer showtime;
    private String title;

    private String header;
    private String description;

    public Content() {

    }

    public Content(String url, Integer showtime, String title) {
        this.url = url;
        this.showtime = showtime;
        this.title = title;
    }

    public Content(String url, Integer showtime, String title, String header, String description) {
        this.url = url;
        this.showtime = showtime;
        this.title = title;
        this.header = header;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getShowtime() {
        return showtime;
    }

    public void setShowtime(Integer showtime) {
        this.showtime = showtime;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.url);
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
        final Content other = (Content) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Content{ url = %s, showtime = %d, header = %s, description = %s }",url,showtime,header,description);
    }

}
