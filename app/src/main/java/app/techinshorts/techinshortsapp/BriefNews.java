package app.techinshorts.techinshortsapp;

/**
 * Created by sp on 25/8/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import org.json.JSONObject;

public class BriefNews extends Fragment {

    private VerticalViewPager verticalViewPager;
    VerticalPagerAdapter adapter;

    RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_style, container, false);

        verticalViewPager = (VerticalViewPager) rootView.findViewById(R.id.verticleViewPager);
        verticalViewPager.setAdapter(adapter = new VerticalPagerAdapter(container.getContext()));



        // Instantiate the cache
    Cache cache = new DiskBasedCache(container.getContext().getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
    Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
    mRequestQueue = new RequestQueue(cache, network);

// Start the queue
    mRequestQueue.start();

    JsonArrayRequest jsObjRequest = new JsonArrayRequest
        (Request.Method.GET, "http://192.168.0.108:3000/news.json", null, new Response.Listener<JSONArray>() {

          @Override
          public void onResponse(JSONArray response) {
            System.out.println("Response: "  + response);
              adapter.addData(response);
          }
        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError error) {
            System.out.println("VolleyError " + error);
            // TODO Auto-generated method stub

          }
        });
    jsObjRequest.setShouldCache(true);
    mRequestQueue.add(jsObjRequest);

        return rootView;
    }

    public JSONObject getCurrentPage() {
        return adapter.data.get(verticalViewPager.getCurrentItem());
    }
}