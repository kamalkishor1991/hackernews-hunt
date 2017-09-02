package com.hnhunt.hnhunt.utils;

/**
 * Created by sp on 25/8/17.
 */


import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrefUtils {
    private static final String TOP_NEWS = "top_news";
    private static final String FIRST_RUN = "first_run";
    private static final int NEWS_CACHE_SIZE = 15;


    public static synchronized void saveTopNews(Context context, JSONArray jsonArray) {
        JSONArray existing =  getTopNews(context);
        JSONArray newList = new JSONArray();
        try {

            int top = existing.length() <= 0 ? -1 : existing.getJSONObject(0).getInt("id");
            for (int i = 0; i < jsonArray.length() && newList.length() < NEWS_CACHE_SIZE; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getInt("id") > top) {
                    newList.put(obj);
                }
            }
            for (int i = 0; i < existing.length() && newList.length() < NEWS_CACHE_SIZE; i++) {
                JSONObject obj = existing.getJSONObject(i);
                newList.put(obj);
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

    public static void setFirstRun(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(FIRST_RUN, false).apply();
    }
    public static boolean isFirstRun(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(FIRST_RUN, true);
    }

}
