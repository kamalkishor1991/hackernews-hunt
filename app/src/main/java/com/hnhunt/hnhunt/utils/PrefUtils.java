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
