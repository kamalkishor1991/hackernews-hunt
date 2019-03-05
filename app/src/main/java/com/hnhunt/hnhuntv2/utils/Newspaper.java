package com.hnhunt.hnhuntv2.utils;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Newspaper {
    private PyObject article;
    private String topImage;
    private String summary;
    private String text;
    private String url;

    public Newspaper(String url) {
        this.url = url;
    }

    public void parse() {
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
        try {
            parseIfGithubRepo();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean parseIfGithubRepo() throws IOException, JSONException {
        URL url = new URL(this.url);
        if (url.getHost().equals("github.com")) {
            if (url.getPath().split("/").length == 3) {
                JSONObject jsonObject = getJSONObjectFromURL("https://api.github.com/repos" + url.getPath());
                this.summary = jsonObject.getString("description");
                this.summary += "\nStars: " + jsonObject.getInt("stargazers_count") + " | Fork Count: " +
                        jsonObject.getInt("forks_count") +
                        " | Language: " + jsonObject.getString("language");
                return true;
            }
        }
        return false;
    }

    public String getSummary() {
        return summary;
    }

    public String getTopImage() {
        return this.topImage;
    }

    private static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }
}
