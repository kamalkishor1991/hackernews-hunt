package com.hnhunt.hnhunt.utils;


import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class PrefUtils {
    private static final String TOP_NEWS = "top_news";
    private static final String FIRST_RUN = "first_run";
    private static final int NEWS_CACHE_SIZE = 15;


    public static synchronized void saveTopNews(Context context, JSONArray jsonArray) {
        JSONArray existing =  getTopNews(context);
        JSONArray newList = new JSONArray();
        try {
            HashSet<Integer> set = new HashSet<>();
            for (int i = 0; i < existing.length(); i++) set.add(existing.getJSONObject(i).getInt("id"));
            JSONArray incoming = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) if (!set.contains(jsonArray.getJSONObject(i).getInt("id"))) incoming.put(jsonArray.getJSONObject(i));

            for (int i = 0, j = 0; newList.length() < NEWS_CACHE_SIZE;) {
                if (i < incoming.length() && j < existing.length()) {

                    if (incoming.getJSONObject(i).getInt("id") < existing.getJSONObject(j).getInt("id")) newList.put(existing.getJSONObject(j++));
                    else newList.put(incoming.getJSONObject(i++));

                }
                else if (i < incoming.length()) newList.put(incoming.getJSONObject(i++));
                else if (j < existing.length()) newList.put(existing.getJSONObject(j++));
                else break;
            }

            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(TOP_NEWS, newList.toString()).apply();

        } catch (JSONException e) {
            Log.d("PrefUtils", e.toString());

        }
    }
    public static JSONArray getTopNews(Context context) {
        try {
            return new JSONArray(PreferenceManager.getDefaultSharedPreferences(context).getString(TOP_NEWS, new JSONArray().toString()));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public static void removeTopNews(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(TOP_NEWS).apply();
    }

    public static void setFirstRun(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(FIRST_RUN, false).apply();
    }
    public static boolean isFirstRun(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(FIRST_RUN, true);
    }

}
