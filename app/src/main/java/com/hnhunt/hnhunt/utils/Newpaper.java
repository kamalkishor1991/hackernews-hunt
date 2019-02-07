package com.hnhunt.hnhunt.utils;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class Newpaper {
    private PyObject article;
    private String title;
    private String topImage;
    private String summary;
    private String text;

    public Newpaper(String url) {
        if (!url.endsWith(".pdf")) {
            Python python = Python.getInstance();
            //python.getModule("");
            // assertTrue(osStr, osStr.contains("module 'os'"));
            this.article = python.getModule("newspaper").get("Article");
            article = article.call(url);
            //article.get("url").toString()
            article.get("download").call();
            // String html = article.get("html").toString();
            article.get("parse").call();
            this.text = article.get("text").toString();
            PyObject punkt = python.getModule("nltk");
            punkt.get("download").call("punkt");
            article.get("nlp").call();
            this.summary = article.get("summary").toString();
            this.topImage = article.get("top_image").toString();
        }
    }

    public String getSummary() {
        return summary;
    }

    public String getTopImage() {
        return this.topImage;
    }


}
