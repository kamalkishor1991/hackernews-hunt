package com.hnhunt.hnhuntv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hnhunt.hnhuntv2.utils.LatestNews;
import com.hnhunt.hnhuntv2.utils.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class VerticalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public static int THRESHOLD = 3;
    public VerticalPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (LatestNews.getInstance().size() == 0) {
            HackerNewsAPI.topNewsStories(context, response -> {
                addData(response);
            }, error -> {
                // FirebaseCrash.log("Network Prob on VerticalPagerAdapter: " + error)
            });
        }
    }

    @Override
    public int getCount() {
        return LatestNews.getInstance().size();
    }

    public void addData(List<Long> list) {
        LatestNews.getInstance().addData(list);
        LatestNews.getInstance().refreshNextPage(mContext, (v) -> {
            notifyDataSetChanged();
        }, (exception) -> {
            //TODO put toast here.
        });
    }
    public Hackernews getHnNews(int position) {
        return LatestNews.getInstance().getHnNews(position);
    }

    public void resetNewData(List<Long> newData) {
        LatestNews.getInstance().resetData(newData);
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.news_card, container, false);

        final Hackernews hackernews = LatestNews.getInstance().getHnNews(position);
        final long hnId = hackernews.getHnId(); //TODO: fix the bug when hnid is coming as null.

        final String title = hackernews.getTitle();
        final String url = hackernews.getURL();
        String host = getHost(url);
        ((TextView)(itemView.findViewById(R.id.title))).setText(title);
        String summary = hackernews.getSummary();//obj.getString("summary");
        if (summary == null || summary.equals("") || summary.equalsIgnoreCase("null")) {
            itemView.findViewById(R.id.summary).setVisibility(View.GONE);
            itemView.findViewById(R.id.missing).setVisibility(View.VISIBLE);
        } else {
            itemView.findViewById(R.id.summary).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.missing).setVisibility(View.GONE);
            ((TextView) (itemView.findViewById(R.id.summary))).setText(summary);
        }

        final SpannableString text = new SpannableString(title + " " + host);
        text.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.grey)), title.length(), title.length() + host.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        text.setSpan(new RelativeSizeSpan(0.75f), title.length(), title.length() + host.length() + 1, 0); // set size
        ((TextView)(itemView.findViewById(R.id.title))).setText(text);

        showTopImage(itemView, hackernews);

        TextView comments = itemView.findViewById(R.id.comments);
        TextView number = itemView.findViewById(R.id.number);
        TextView points = itemView.findViewById(R.id.points);
        number.setText("#" + (position + 1));
        setCommentAndPoints(comments, points, "" + hackernews.getDecedents(), "" + hackernews.getScore());
        ((TextView) itemView.findViewById(R.id.time)).setText("Published: " +
                Utility.formatTime(new Date(hackernews.getEpochTimeMs())));
        ImageButton imageButton = itemView.findViewById(R.id.share);
        Bitmap originalBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share);

        BlurMaskFilter blurFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
        Bitmap shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY);


        imageButton.setImageBitmap(shadowImage);

        itemView.findViewById(R.id.share_bg).setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, title);
            share.putExtra(Intent.EXTRA_TEXT, url);

            mContext.startActivity(Intent.createChooser(share, "Share this item!"));
            Bundle hnShare = new Bundle();
            hnShare.putLong("hn_id", hnId);
            hnShare.putString("title", title);
            FirebaseAnalytics.getInstance(mContext).logEvent("Share_Intent", hnShare);
        });

        container.addView(itemView);
        updateCommentsAndPoints(hackernews, comments, points);
        if (position + THRESHOLD == LatestNews.getInstance().getLastUpdatedIndex()) {
            LatestNews.getInstance().refreshNextPage(mContext, (v) -> {
                notifyDataSetChanged();
            }, e -> {
                //FirebaseCrash.log("Network Prob after threshold: " + e);
            });

        }


        return itemView;
    }

    private void showTopImage(View itemView, Hackernews hackernews) {
        if (hackernews.getTopImage() != null && !hackernews.getTopImage().isEmpty()) {
            Picasso.with(mContext).load(hackernews.getTopImage())
                    .placeholder(R.drawable.background_default)
                    .fit()
                    .centerInside()
                    .into((ImageView) itemView.findViewById(R.id.profileImageView));

            Picasso.with(mContext).load(hackernews.getTopImage())
                    .placeholder(R.drawable.background_default)
                    .resize(7, 7)
                    .centerInside()
                    .into((ImageView) itemView.findViewById(R.id.profileBK));
        }
    }

    @NonNull
    private String getHost(String url) {
        String host = null;
        try {
            host = "(" + new URL(url).getHost() + ")";
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return host;
    }

    private void setCommentAndPoints(TextView comments, TextView points, String commentCount, String score)  {
        comments.setText(commentCount + " comments");
        points.setText(score +  " points");
    }

    private void updateCommentsAndPoints(final Hackernews hackernews, final TextView comments, final TextView points) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://hacker-news.firebaseio.com/v0/item/" + hackernews.getHnId() + ".json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String cts = response.getString("descendants");
                    String score = response.getInt("score") + "";
                    setCommentAndPoints(comments, points, cts, score);
                    hackernews.setDecedents(Long.parseLong(cts));
                    hackernews.setScore(Long.parseLong(score));
                    hackernews.saveAsync();
                } catch (JSONException e) {
                    e.printStackTrace();
                    // FirebaseCrash.log("Unable to fetch comments and points from hn api: " + e);
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
