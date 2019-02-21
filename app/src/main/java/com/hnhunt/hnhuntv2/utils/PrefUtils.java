package com.hnhunt.hnhuntv2.utils;


import android.content.Context;
import android.preference.PreferenceManager;

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
