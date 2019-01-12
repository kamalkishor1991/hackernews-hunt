package com.hnhunt.hnhunt;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;

import com.hnhunt.hnhunt.utils.LatestNews;
import com.hnhunt.hnhunt.utils.Utility;

public class SplashActivity extends AppCompatActivity {
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Python python = Python.getInstance();
        //python.getModule("");
        PyObject os = python.getModule("os");
        String osStr = os.toString();
        // assertTrue(osStr, osStr.contains("module 'os'"));
        PyObject osPath = python.getModule("os.path");
        String sttt = os.get("path").toString();
        PyObject article = python.getModule("newspaper").get("Article");
        article = article.call("http://fox13now.com/2013/12/30/new-year-new-laws-obamacare-pot-guns-and-drones/");
        //article.get("url").toString()
        article.get("download").call();
        String html = article.get("html").toString();
        article.get("parse").call();
        String text = article.get("text").toString();
        PyObject punkt = python.getModule("nltk");
        punkt.get("download").call("punkt");
        article.get("nlp").call();
        String summary = article.get("summary").toString();


        Utility.fetchNews( getApplicationContext(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                LatestNews.getInstance().resetData(response);
                launchMainActivity();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrash.log("Network Prob on splash: " + error);
                Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_SHORT).show();
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
