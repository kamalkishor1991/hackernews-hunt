package com.hnhunt.hnhunt;

import java.util.List;

public class HnNews {
    private String by;
    private long decendents;
    private long id;
    private List<Long> kids;
    private long time;
    private String title;
    private String type;
    private long score;
    private String url;

    private String summary;
    private String topImage;


    public HnNews(String by, long decendents, long id, List<Long> kids, long time,
                  String title, String type, long score, String url) {
        this.by = by;
        this.decendents = decendents;
        this.id = id;
        this.kids = kids;
        this.time = time;
        this.title = title;
        this.type = type;
        this.score = score;
        this.url = url;
    }


    public String getTitle() {
        return title;
    }

    public String getURL() {

        return url;
    }

    public String getCommentURL() {
        return "https://news.ycombinator.com/item?id=" + id;
    }

    public long getHnId() {
        return id;
    }

    public String getBy() {
        return by;
    }

    public long getDecendents() {
        return decendents;
    }

    public List<Long> getKids() {
        return kids;
    }

    public long getTime() {
        return time;
    }

    public long getEpochTimeMs() {
        return time * 1000;
    }


    public String getType() {
        return type;
    }

    public long getScore() {
        return score;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTopImage() {
        return topImage;
    }

    public void setTopImage(String topImage) {
        this.topImage = topImage;
    }
}
