package app.techinshorts.techinshortsapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import app.techinshorts.techinshortsapp.utils.PrefUtils;
import app.techinshorts.techinshortsapp.utils.Utility;

public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String type = data.getString("type");
        String message = data.getString("message");
        String summary = data.getString("summary");
        String imgURL = data.getString("image_url");
        switch (type)  {
            case "update":
                updateNews();
                break;
            case "notification":
                sendNotification(message, summary, imgURL);
                break;
        }
    }

    private void updateNews() {
        Utility.fetchNews( getApplicationContext(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                PrefUtils.saveTopNews(getApplicationContext(), response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrash.log("VolleyError: " + error);
            }
        });
    }
    //This method is generating a notification and displaying the notification
    private void sendNotification(String message, String summary, String imageURL) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap remote_picture = null;

// Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new
                NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(message);
        notiStyle.setSummaryText(summary);


        try {
            remote_picture = BitmapFactory.decodeStream(
                    (InputStream) new URL(imageURL).getContent());
            //remote_picture = Bitmap.createScaledBitmap(remote_picture, 400, 200, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(remote_picture)
                .setContentText(message)
                .setAutoCancel(true).setStyle(notiStyle)
                .setContentIntent(pendingIntent);
// Add the big picture to the style.
        notiStyle.bigPicture(remote_picture);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), noBuilder.build()); //0 = ID of notification
    }
}