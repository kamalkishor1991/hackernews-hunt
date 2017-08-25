package app.techinshorts.techinshortsapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sp on 21/8/17.
 */
public class VerticalPagerAdapter extends PagerAdapter {

    ArrayList<JSONObject> data;
    Context mContext;
    LayoutInflater mLayoutInflater;

    public VerticalPagerAdapter(Context context) {
        mContext = context;
        data = new ArrayList<>();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void addData(JSONArray list) {
        for (int i = 0; i < list.length(); i++) {
            try {
                data.add(list.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.news_card, container, false);



        JSONObject obj = data.get(position);
        try {
            ((TextView)(itemView.findViewById(R.id.title))).setText(obj.getString("title"));
            ((TextView)(itemView.findViewById(R.id.summary))).setText(obj.getString("summary"));

            Picasso.with(mContext).load(obj.getString("top_image")).into((ImageView)itemView.findViewById(R.id.profileImageView));

        } catch (Exception e) {
            e.printStackTrace();
        }

        container.addView(itemView);


        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}