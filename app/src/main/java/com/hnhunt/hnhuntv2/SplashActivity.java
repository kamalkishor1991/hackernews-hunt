package com.hnhunt.hnhuntv2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.crash.FirebaseCrash;
import com.hnhunt.hnhuntv2.utils.LatestNews;

import java.util.List;

public class SplashActivity extends AppCompatActivity {
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showLoading();
        HackerNewsAPI.topNewsStories(this, response -> {
            addData(response);
        }, error -> FirebaseCrash.log("Network Prob on VerticalPagerAdapter: " + error));
    }

    public void addData(List<Long> list) {
        LatestNews.getInstance().addData(list);
        LatestNews.getInstance().refreshNextPage(this, (v) -> {
            launchMainActivity();
        }, (exception) -> {
            //TODO put toast here.
        });
    }

    private void showLoading() {
        ProgressDialog loading  = new ProgressDialog(this);
        loading.setCancelable(true);
        loading.setMessage("Loading ..");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();
    }

    private void launchMainActivity() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
