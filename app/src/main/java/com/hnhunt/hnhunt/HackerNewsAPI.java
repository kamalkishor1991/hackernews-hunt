package com.hnhunt.hnhunt;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HackerNewsAPI {
    public static void topNewsStories(Context context, Consumer<List<Long>> result, Consumer<Exception> exception) {
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
            (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/topstories.json",
                    null, (res) -> {
                List<Long> hnIds = new ArrayList<>();
                for (int i = 0; i < res.length(); i++) {
                    try {
                        hnIds.add(res.getLong(i));
                    } catch (JSONException e) {
                        exception.accept(new RuntimeException(e));
                        return;
                    }
                }
                result.accept(hnIds);
            }, (e) -> {
                exception.accept(e);
            });
        mRequestQueue.add(jsObjRequest);
    }


    public static void getStory(Context context, long hnId,
                         Consumer<HnNews> result, Consumer<Exception> exception) {
        String url = "https://hacker-news.firebaseio.com/v0/item/" + hnId +  ".json";
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                        null, (res) -> {
                try {
                    HnNews hnNews = new HnNews(hnId, res.getString("url"));
                    result.accept(hnNews);
                } catch (JSONException e) {
                    e.printStackTrace();
                    exception.accept(e);
                }
            }, (e) -> {
                exception.accept(e);
            });
        mRequestQueue.add(jsObjRequest);
    }



}
