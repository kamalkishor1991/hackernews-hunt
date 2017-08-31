package app.techinshorts.techinshortsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.techinshorts.techinshortsapp.utils.PrefUtils;

public class BriefNews extends Fragment {

    private VerticalViewPager verticalViewPager;
    private VerticalPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(container.getContext());

        View rootView = inflater.inflate(R.layout.fragment_style, container, false);

        verticalViewPager = (VerticalViewPager) rootView.findViewById(R.id.verticleViewPager);
        verticalViewPager.setAdapter(adapter = new VerticalPagerAdapter(container.getContext()));

        verticalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Bundle bundle = new Bundle();

                bundle.putString("position", position + "");
                mFirebaseAnalytics.logEvent("OnCreateViewBriefNews", bundle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    public JSONObject getCurrentPage() {
        try {
            return adapter.getData().getJSONObject(verticalViewPager.getCurrentItem());
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.log("exception while getting data: " + e);
        }
        return null;
    }
}