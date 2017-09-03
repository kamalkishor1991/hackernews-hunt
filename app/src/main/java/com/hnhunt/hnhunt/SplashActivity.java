package com.hnhunt.hnhunt;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;

import com.hnhunt.hnhunt.utils.PrefUtils;
import com.hnhunt.hnhunt.utils.Utility;

public class SplashActivity extends AppCompatActivity {
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (PrefUtils.isFirstRun(getApplicationContext())) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchMainActivity();
                }
            }, 3000);
        } else {
            showLoading();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchMainActivity();
                }
            }, 1000);

        }
        Utility.fetchNews( getApplicationContext(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                PrefUtils.saveTopNews(getApplicationContext(), response);
                PrefUtils.setFirstRun(getApplicationContext());
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrash.log("On error fetch news: " + error.toString());
            }
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
