package com.hnhunt.hnhunt;


import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hnhunt.hnhunt.utils.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;


public class FCMTokenRefreshListenerService extends FirebaseInstanceIdService {

    //If the token is changed registering the device again
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Firebase token refresh", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        JSONObject json = new JSONObject();
        Date now = new Date();
        //Import part : x.0 for double number
        double offsetFromUtc = TimeZone.getDefault().getOffset(now.getTime()) / 3600000.0;
        try {
            json.put("timezone_str",  TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
            json.put("offset_gmt", offsetFromUtc );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendRegistrationToServer(refreshedToken, json.toString());

    }
    private void sendRegistrationToServer(String device_token, String timezone)  {

        SyncHttpClient client = new SyncHttpClient();
        Map<String, String> map = new HashMap<>();
        map.put("device[device_token]", device_token);
        map.put("device[timezone]", timezone);
        RequestParams requestParams = new RequestParams(map);
        String request        = Utility.BASE_URL + "/devices";
        client.post(request, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("successfully submitted device token");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Failed to send device token.");
            }
        });
    }
}