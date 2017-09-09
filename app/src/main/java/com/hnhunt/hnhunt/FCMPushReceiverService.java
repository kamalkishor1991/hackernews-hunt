package com.hnhunt.hnhunt;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hnhunt.hnhunt.utils.PrefUtils;
import com.hnhunt.hnhunt.utils.Utility;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class FCMPushReceiverService extends FirebaseMessagingService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Getting the message from the bundle

        Map<String, String> data = remoteMessage.getData();
        String type = data.get("type");
        String message = data.get("message");
        String imgURL = data.get("image_url");
        String id = data.get("id");
        switch (type)  {
            case "notification":
                sendNotification(message, imgURL, id);
                break;
            case "clear_cache":
                PrefUtils.removeTopNews(getApplicationContext());
                break;
        }
    }

    //This method is generating a notification_small and displaying the notification_small
    private void sendNotification(String message, String imageURL, String id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("id", id);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
       // Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap remote_picture = null;
       // NotificationCompat.
// Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new
                NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(message);
        notiStyle.setSummaryText(message);
        //notiStyle.setSummaryText(summary);

       /* NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(message);
        bigTextStyle.bigText(summary);*/
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_small);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);

        final String additionalInfo = "  & more stories...";
        final SpannableString text = new SpannableString(message + additionalInfo);
        final SpannableString textSmall = new SpannableString(message + additionalInfo);
        textSmall.setSpan(new ForegroundColorSpan(getApplicationContext().getResources().getColor(R.color.grey)), message.length(), message.length() + additionalInfo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        text.setSpan(new RelativeSizeSpan(0.75f), message.length(), message.length() + additionalInfo.length(), 0); // set size
        textSmall.setSpan(new RelativeSizeSpan(0.75f), message.length(), message.length() + additionalInfo.length(), 0); // set size


        contentView.setTextViewText(R.id.title, textSmall);

        RemoteViews contentViewBig = new RemoteViews(getPackageName(), R.layout.notification_big);
        text.setSpan(new ForegroundColorSpan(getApplicationContext().getResources().getColor(R.color.white)), message.length(), message.length() + additionalInfo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        contentViewBig.setTextViewText(R.id.text_big, text);
        try {
            remote_picture = BitmapFactory.decodeStream(
                    (InputStream) new URL(imageURL).getContent());

            //remote_picture = Bitmap.createScaledBitmap(remote_picture, 400, 200, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        remote_picture = Bitmap.createScaledBitmap(remote_picture, 390, 230, false);
        Bitmap compressPicture = Bitmap.createScaledBitmap(remote_picture, 5, 3, false);
        contentViewBig.setImageViewBitmap(R.id.big_picture, remote_picture);
        contentViewBig.setImageViewBitmap(R.id.big_picture_bg, compressPicture);
        //contentViewBig.setTextViewText(R.id.big_text, "kdasfjlk asdflkjdsa jflkdsajflkj dsafjdsajflksajd;lkfjsad;jflkdsajflk;saj;lkfjsad fjdsajf;lkdsaj;lkfjsafj;ldsajf;lkdsajf;lksajd;fj");

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setVisibility(VISIBILITY_PUBLIC)
                .setContent(contentView)
                .setAutoCancel(true)
                .setCustomBigContentView(contentViewBig)
                .setContentIntent(pendingIntent);
// Add the big picture to the style.
        notiStyle.bigPicture(remote_picture);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), noBuilder.build()); //0 = ID of notification_small
    }
}