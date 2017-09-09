package com.hnhunt.hnhunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.hnhunt.hnhunt.utils.LatestNews;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import com.hnhunt.hnhunt.utils.PrefUtils;
import com.hnhunt.hnhunt.utils.Utility;
import cz.msebera.android.httpclient.Header;


public class VerticalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public static int THRESHOLD = 6;
    public VerticalPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LatestNews.getInstance().getData().length() <= 1) {
            Utility.fetchNews(context, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    addData(response);
                    notifyDataSetChanged();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getCount() {
        return LatestNews.getInstance().getData().length();
    }

    public void addData(JSONArray list) {
        try {
            LatestNews.getInstance().addData(list);
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONArray getData() {
        return LatestNews.getInstance().getData();
    }

    public void resetNewData(JSONArray newData) {
        LatestNews.getInstance().resetData(newData);
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.news_card, container, false);
        try {
            JSONObject obj = LatestNews.getInstance().getData().getJSONObject(position);
            final String title = obj.getString("title");
            final String url = obj.getString("url");
            final long hn_id = obj.getLong("hn_id");
            String host = "(" + new URL(url).getHost() + ")";
            ((TextView)(itemView.findViewById(R.id.title))).setText(title);
            ((TextView)(itemView.findViewById(R.id.summary))).setText(obj.getString("summary"));

            final SpannableString text = new SpannableString(title + " " + host);
            text.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.grey)), title.length(), title.length() + host.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text.setSpan(new RelativeSizeSpan(0.75f), title.length(), title.length() + host.length() + 1, 0); // set size
            ((TextView)(itemView.findViewById(R.id.title))).setText(text);

            Picasso.with(mContext).load(obj.getString("top_image"))
                    .placeholder(R.drawable.background_default)
                    .fit()
                    .centerInside()
                    .into((ImageView)itemView.findViewById(R.id.profileImageView));

            Picasso.with(mContext).load(obj.getString("top_image"))
                    .placeholder(R.drawable.background_default)
                    .resize(7,7)
                    .centerInside()
                    .into((ImageView)itemView.findViewById(R.id.profileBK));

            TextView comments = (TextView) itemView.findViewById(R.id.comments);
            TextView points = (TextView) itemView.findViewById(R.id.points);
            setCommentAndPoints(comments, points, obj.getString("comment_count") , obj.getString("score") );
            ((TextView) itemView.findViewById(R.id.time)).setText("Published: " + Utility.formatTime(new Date(obj.getLong("epoch") * 1000)));
            updateCommentsAndPoints(hn_id, comments, points);
            ImageButton imageButton = (ImageButton) itemView.findViewById(R.id.share);
            Bitmap originalBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share);

            BlurMaskFilter blurFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);
            Paint shadowPaint = new Paint();
            shadowPaint.setMaskFilter(blurFilter);

            int[] offsetXY = new int[2];
            Bitmap shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY);


            imageButton.setImageBitmap(shadowImage);

            ((ImageButton) itemView.findViewById(R.id.share_bg)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

                    // Add data to the intent, the receiving app will decide
                    // what to do with it.
                    share.putExtra(Intent.EXTRA_SUBJECT, title);
                    share.putExtra(Intent.EXTRA_TEXT, url);

                    mContext.startActivity(Intent.createChooser(share, "Share this item!"));
                    Bundle hnShare = new Bundle();
                    hnShare.putLong("hn_id", hn_id);
                    hnShare.putString("title", title);
                    FirebaseAnalytics.getInstance(mContext).logEvent("Share_Intent", hnShare);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        container.addView(itemView);
        if (position + THRESHOLD == LatestNews.getInstance().getData().length()) {
            Utility.fetchNews(mContext, "" + LatestNews.getInstance().getIncPage(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        LatestNews.getInstance().addData(response);
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }


        return itemView;
    }

    private void setCommentAndPoints(TextView comments, TextView points, String comment_count, String score)  {
        comments.setText(comment_count + " comments");
        points.setText(score +  " points");
    }

    private void updateCommentsAndPoints(final long hnId, final TextView comments, final TextView points) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://hacker-news.firebaseio.com/v0/item/" + hnId + ".json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String cts = response.getString("descendants");
                    String score = response.getInt("score") + "";
                    setCommentAndPoints(comments, points, cts, score);
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.log("Unable to fetch comments and points from hn api: " + e);
                    //TODO: put toast here ~ unable to update comments and points info
                }
            }
        });

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}