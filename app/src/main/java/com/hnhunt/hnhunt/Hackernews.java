package com.hnhunt.hnhunt;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

import java.util.List;
@Table(name = "Hackernews", database = AppDatabase.class)
public class Hackernews extends Model {

    @PrimaryKey
    private long id;
    @Column(name = "by")
    private String by;
    @Column(name = "decedents")
    private long decedents;
    private List<Long> kids;
    @Column(name = "time")
    private long time;
    @Column (name = "title")
    private String title;
    @Column (name = "type")
    private String type;
    @Column (name = "score")
    private long score;
    @Column (name = "url")
    private String url;
    @Column (name = "summary")
    private String summary;
    @Column (name = "top_image")
    private String topImage;

    public Hackernews() {

    }
    public Hackernews(String by, long decedents, long id, List<Long> kids, long time,
                      String title, String type, long score, String url) {
        this.by = by;
        this.decedents = decedents;
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

    public long getDecedents() {
        return decedents;
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

    public void setDecedents(long decedents) {
        this.decedents = decedents;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
