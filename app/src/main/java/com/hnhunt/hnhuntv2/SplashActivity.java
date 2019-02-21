package com.hnhunt.hnhuntv2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchMainActivity();
        /*Utility.fetchNews( getApplicationContext(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                LatestNews.getInstance().resetData(response);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrash.log("Network Prob on splash: " + error);
                Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_SHORT).show();
            }
        });*/
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
