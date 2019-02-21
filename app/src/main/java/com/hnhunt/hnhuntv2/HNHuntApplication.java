package com.hnhunt.hnhuntv2;

import android.app.Application;
import android.content.res.Configuration;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;

public class HNHuntApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this.getApplicationContext()));
        }
        DatabaseConfig appDatabase = new DatabaseConfig.Builder(AppDatabase.class).addModelClasses(Hackernews.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabase)
                .build());
        // Required initialization logic here!
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
