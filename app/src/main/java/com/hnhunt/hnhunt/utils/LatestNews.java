package com.hnhunt.hnhunt.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by sp on 8/9/17.
 */

public class LatestNews {
    private List<Long> data;
    HashSet<Integer> newsIds;
    private int page = 1;
    private static LatestNews latestNews;
    private LatestNews() {
        data = new ArrayList<>();
        newsIds = new HashSet<>();
        page = 1;
    }

    public static LatestNews getInstance() {
        if (latestNews != null) return latestNews;
        return latestNews = new LatestNews();
    }
    public List<Long> getData() {
        return data;
    }

    public void resetData(List<Long> d) {
        data = d;
        page = 1;
        /*for (int i = 0; i< data.length(); i++) {
            try {
                newsIds.add(data.getJSONObject(i).getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

    }

    public void addSingleObj(JSONObject obj) {
       /* try {
            data.put(obj);
            newsIds.add(obj.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    public int getIncPage() {
        return ++page;
    }

    public void addData(JSONArray news) throws JSONException {
       /* for (int i = 0; i < news.length(); i++) {
            if (newsIds.contains(news.getJSONObject(i).getInt("id"))) continue;
            data.put(news.getJSONObject(i));
        }*/
    }

    public void addData(List<Long> news) throws JSONException {
        this.data = news;
        for (int i = 0; i < news.size(); i++) {
            // if (newsIds.contains(news.getJSONObject(i).getInt("id"))) continue;
            // data.put(news.getJSONObject(i));
        }
    }
}
