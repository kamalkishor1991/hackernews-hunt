package com.hnhunt.hnhunt;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class NewsFragment extends Fragment {

    private VerticalViewPager verticalViewPager;
    private VerticalPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(container.getContext());

        View rootView = inflater.inflate(R.layout.fragment_style, container, false);

        verticalViewPager = rootView.findViewById(R.id.verticleViewPager);
        verticalViewPager.setAdapter(adapter = new VerticalPagerAdapter(container.getContext()));

        final SwipeRefreshLayout swipeView = rootView.findViewById(R.id.swipe);
        //verticalViewPager.
        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) swipeView.setEnabled(true);
                else swipeView.setEnabled(false);

                Bundle bundle = new Bundle();

                bundle.putString("position", position + "");
                mFirebaseAnalytics.logEvent("OnCreateViewBriefNews", bundle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        swipeView.setOnRefreshListener(() -> {
            swipeView.setRefreshing(true);
            HackerNewsAPI.topNewsStories(container.getContext(), (result) -> {
                adapter.resetNewData(result);
                swipeView.setRefreshing(false);
            }, (exception) -> {
                swipeView.setRefreshing(false);
            });
        });

        return rootView;
    }

    public Hackernews getCurrentPage() {
        return adapter.getHnNews(verticalViewPager.getCurrentItem());
    }
}