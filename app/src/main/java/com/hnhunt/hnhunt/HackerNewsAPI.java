package com.hnhunt.hnhunt;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.crash.FirebaseCrash;
import com.hnhunt.hnhunt.utils.Newspaper;
import com.reactiveandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

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
                    runOnUIThread(context, () -> result.accept(hnIds));
                }, (e) -> exception.accept(e));
        mRequestQueue.add(jsObjRequest);
    }


    private static void getStory(long hnId, JSONObject response,
                                 Consumer<Hackernews> result, Consumer<Exception> exception) {

        Select.from(Hackernews.class).where("id = ?", 1).fetchAsync().
                doOnError((e) ->  {
                    exception.accept(new RuntimeException(e));
                }).
                subscribeOn(Schedulers.io()).subscribe((List<Hackernews> hnNews) -> {
                    if (hnNews.isEmpty()) {
                        fetchStory(hnId, response, result, exception);
                    } else {
                        result.accept(hnNews.get(0));
                    }
                });
    }

    private static void fetchStory(long hnId, JSONObject response, Consumer<Hackernews> result, Consumer<Exception> exception) {
        try {
            Log.i("Hackernews api", " id=" + hnId);
            String by = response.getString("by");
            List<Long> kids = new ArrayList<>();
            long cts = response.has("descendants") ? Long.parseLong(response.getString("descendants")) : 0;

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
            Hackernews hackernews = new Hackernews(by, cts, hnId, kids, time, title, type, score, url);
            try {
                Newspaper newspaper = new Newspaper(hackernews.getURL());
                newspaper.parse();
                hackernews.setSummary(newspaper.getSummary());
                hackernews.setTopImage(newspaper.getTopImage());
            } catch (Throwable e) {
                FirebaseCrash.log("Unable to fetch summary: " + e);
            }
            hackernews.save();
            result.accept(hackernews);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.log("Unable to fetch comments and points from hn api: " + e);
            exception.accept(e);
        }
    }

    public static void getStory(Context context, long hnId,
                                Consumer<Hackernews> result, Consumer<Exception> exception) {
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,"https://hacker-news.firebaseio.com/v0/item/" + hnId + ".json", null, (res) -> {
                    runOnUIThread(context, () -> getStory(hnId, res, result, exception ));
                }, (error) -> {} );
        mRequestQueue.add(jsObjRequest);
    }
    private static void runOnUIThread(Context context, Runnable runnable) {
        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(runnable);
    }

}
