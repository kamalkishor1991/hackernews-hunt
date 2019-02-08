package com.hnhunt.hnhunt;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.crash.FirebaseCrash;
import com.hnhunt.hnhunt.utils.Newpaper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cz.msebera.android.httpclient.Header;

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


    public static void getStory(long hnId,
                         Consumer<HnNews> result, Consumer<Exception> exception) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://hacker-news.firebaseio.com/v0/item/" + hnId + ".json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i("Hackernews api", " id=" + hnId);
                    String by = response.getString("by");
                    List<Long> kids = new ArrayList<>();
                    long cts = Long.parseLong(response.getString("descendants"));

                    JSONArray kidsArray = response.optJSONArray("kids");
                    if (kidsArray != null) {
                        for (int i = 0; i < kidsArray.length(); i++) {
                            kids.add(kidsArray.getLong(i));
                        }
                    }
                    long score = response.getLong("score");
                    long time = response.getLong("time");
                    String title = response.getString("title");
                    String type = response.getString("type");
                    String url = response.optString("url", "https://news.ycombinator.com/item?id=" + hnId);
                    HnNews hnNews = new HnNews(by, cts, hnId, kids, time, title, type, score, url);
                    try {
                        Newpaper newpaper = new Newpaper(hnNews.getURL());
                        newpaper.parse();
                        hnNews.setSummary(newpaper.getSummary());
                        hnNews.setTopImage(newpaper.getTopImage());
                    } catch (Throwable e) {
                        //Logger.getLogger(this.getClass().getName()).log(e.toString() + "");
                    }
                    result.accept(hnNews);
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.log("Unable to fetch comments and points from hn api: " + e);
                    exception.accept(e);
                    //TODO: put toast here ~ unable to update comments and points info
                }
            }
        });
    }



}
