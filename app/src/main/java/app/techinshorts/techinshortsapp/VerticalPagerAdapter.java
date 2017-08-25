package app.techinshorts.techinshortsapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.techinshorts.techinshortsapp.utils.PrefUtils;
import app.techinshorts.techinshortsapp.utils.Utility;

/**
 * Created by sp on 21/8/17.
 */
public class VerticalPagerAdapter extends PagerAdapter {

    private JSONArray data;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public static int THRESHOLD = 4;

    public VerticalPagerAdapter(Context context) {
        mContext = context;
        data = PrefUtils.getTopNews(context);
        loadData(context);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length();
    }

    public void addData(JSONArray list) {
        for (int i = 0; i < list.length(); i++) {
            try {
                data.put(list.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public JSONArray getData() {
        return data;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.news_card, container, false);

        try {
            JSONObject obj = data.getJSONObject(position);
            ((TextView)(itemView.findViewById(R.id.title))).setText(obj.getString("title"));
            ((TextView)(itemView.findViewById(R.id.summary))).setText(obj.getString("summary"));

            Picasso.with(mContext).load(obj.getString("top_image")).into((ImageView)itemView.findViewById(R.id.profileImageView));

        } catch (Exception e) {
            e.printStackTrace();
        }

        container.addView(itemView);
        if (position + THRESHOLD == data.length()) {
            try {
                loadData(container.getContext(), false, true, data.getJSONObject(data.length() - 1).getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    private void loadData(final Context context) {
        loadData(context, true, false, "100000000");

    }
    private void loadData(final Context context, final boolean cacheAdd, final boolean memoryAdd, String offset) {


        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.

        RequestQueue mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, Utility.fetchApi("offset", offset), null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        if (cacheAdd)
                        PrefUtils.saveTopNews(context, response);
                        if (data.length() == 0 || memoryAdd)
                            addData(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleyError ", error.toString());
                        // TODO Auto-generated method stub

                    }
                });
        jsObjRequest.setShouldCache(true);
        mRequestQueue.add(jsObjRequest);
    }
}