package app.techinshorts.techinshortsapp;

/**
 * Created by sp on 25/8/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import org.json.JSONException;
import org.json.JSONObject;

import app.techinshorts.techinshortsapp.utils.PrefUtils;

public class BriefNews extends Fragment {

    private VerticalViewPager verticalViewPager;
    VerticalPagerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_style, container, false);

        verticalViewPager = (VerticalViewPager) rootView.findViewById(R.id.verticleViewPager);
        verticalViewPager.setAdapter(adapter = new VerticalPagerAdapter(container.getContext()));



        return rootView;
    }

    public JSONObject getCurrentPage() {
        try {
            return adapter.getData().getJSONObject(verticalViewPager.getCurrentItem());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}