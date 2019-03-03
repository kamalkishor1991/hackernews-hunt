package com.hnhunt.hnhuntv2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.hnhunt.hnhuntv2.utils.LatestNews;

import java.util.List;

public class SplashActivity extends AppCompatActivity {
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ProgressDialog pd = showLoading("Fetching top news..");
        HackerNewsAPI.topNewsStories(this, response -> {
            pd.cancel();
            addData(response);
        }, error -> FirebaseCrash.log("Network Prob on VerticalPagerAdapter: " + error));
    }

    public void addData(List<Long> list) {
        final ProgressDialog pd = showLoading("Generating summary");
        LatestNews.getInstance().addData(list);
        LatestNews.getInstance().refreshNextPage(this, (v) -> {
            pd.cancel();
            launchMainActivity();
        }, (exception) -> {
            Toast.makeText( this,"Unable to fetch news.", Toast.LENGTH_SHORT);
        });
    }

    private ProgressDialog showLoading(String msg) {
        ProgressDialog loading  = new ProgressDialog(this);
        loading.setCancelable(true);
        loading.setMessage(msg);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();
        return loading;
    }

    private void launchMainActivity() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
