package com.hnhunt.hnhunt;

public class HnNews {
    private String url;
    private String commentURL;
    private String commentCount;
    private long hnId;
    public HnNews(long hnID, String url) {
        this.hnId = hnID;
        this.commentURL = "https://news.ycombinator.com/item?id=" + hnID;
        this.url = url;
    }


}
