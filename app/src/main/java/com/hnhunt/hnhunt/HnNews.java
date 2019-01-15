package com.hnhunt.hnhunt;

public class HnNews {
    private String url;
    private String commentURL;
    private String commentCount;
    private long hnId;
    private String title;
    public HnNews(long hnID, String title, String url) {
        this.hnId = hnID;
        this.commentURL = "https://news.ycombinator.com/item?id=" + hnID;
        this.url = url;
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

    public String getURL() {
        return url;
    }

    public String getCommentURL() {
        return commentURL;
    }

    public long getHnId() {
        return hnId;
    }
}
