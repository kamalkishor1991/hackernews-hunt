package app.techinshorts.techinshortsapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;


public class Utility {
    public static final String BASE_URL = "http://hnhunt.com";
    public static String fetchApi(String... args) {
        String url = BASE_URL + "/news.json";
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i + 1] == null) continue;
            params.append(i == 0 ? "" : "&").append(args[i]).append("=").append(args[i + 1]);
        }
        if (params.length() > 0) {
            url = url + "?" + params.toString();
        }
        return url;
    }

    public static void fetchNews(final Context context, String offset, Response.Listener<JSONArray> successListener, Response.ErrorListener errorListener) {


        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.

        RequestQueue mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, Utility.fetchApi("offset", offset), null, successListener, errorListener);
        jsObjRequest.setShouldCache(true);
        mRequestQueue.add(jsObjRequest);
    }

    public static String formatTime(Date date) {
        PrettyTime p = new PrettyTime();
        return p.format(date);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
