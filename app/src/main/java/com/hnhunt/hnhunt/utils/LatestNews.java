package com.hnhunt.hnhunt.utils;

import com.hnhunt.hnhunt.Consumer;
import com.hnhunt.hnhunt.HackerNewsAPI;
import com.hnhunt.hnhunt.HnNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class LatestNews {
    private List<Long> data;
    HashSet<Integer> newsIds;
    private final Map<Long, HnNews> hnNewsHashMap;
    private int lastIndex = 0;
    private static LatestNews latestNews;
    private LatestNews() {
        data = new ArrayList<>();
        newsIds = new HashSet<>();
        hnNewsHashMap = new ConcurrentHashMap<>();
    }

    public static LatestNews getInstance() {
        if (latestNews != null) return latestNews;
        return latestNews = new LatestNews();
    }
    public List<Long> getData() {
        return data;
    }

    public int getLastUpdatedIndex() {
        return lastIndex;
    }
    public HnNews getHnNews(int position) {
        return hnNewsHashMap.get(data.get(position));
    }
    public void addData(List<Long> news) {
        this.data = news;
    }

    public void resetData(List<Long> newData) {
    }

    public void refreshNextPage(Consumer<Void> callback, Consumer<Exception> exceptionConsumer) {
        final Exception[] lastException = new Exception[1];
        int pageSize = 5;
        CountDownLatch countDownLatch = new CountDownLatch(pageSize);
        for (int i = lastIndex;i < Math.min(lastIndex + pageSize, data.size()); i++) {
            long hnId = data.get(i);
            HackerNewsAPI.getStory(hnId, (hn) -> {
                hnNewsHashMap.put(hnId, hn);
                countDownLatch.countDown();
            }, (ex) -> {
                lastException[0] = ex;
            });
        }

        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                exceptionConsumer.accept(e);
            }
            if (lastException[0] == null) {
                callback.accept(null);
                lastIndex += pageSize;
            } else {
                exceptionConsumer.accept(lastException[0]);
            }
        }).start();


    }
}
