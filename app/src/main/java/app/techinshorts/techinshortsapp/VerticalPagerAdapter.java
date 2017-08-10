package app.techinshorts.techinshortsapp;


        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Color;
        import android.support.v4.view.PagerAdapter;
        import android.text.Layout;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout.LayoutParams;
        import android.widget.TextView;

        import org.json.JSONArray;
        import org.json.JSONException;

        import java.io.IOException;
        import java.io.InputStream;

public class VerticalPagerAdapter extends PagerAdapter{

    private Context mContext;

    private int mParent;
    private int mChilds;
    private JSONArray mColors;

    public VerticalPagerAdapter(Activity c, int parent, int childs){
        mContext = c;
        mParent = parent;
        mChilds = childs;
        loadJSONFromAsset(c);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mChilds;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LinearLayout linear = new LinearLayout(mContext);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linear.setLayoutParams(lp);
        View linearLayout = LayoutInflater.from(mContext).inflate(R.layout.news_card, null);
        linear.addView(linearLayout);

        TextView tvChild = new TextView(mContext);
        tvChild.setGravity(Gravity.CENTER_HORIZONTAL);
        tvChild.setText("Child:" + position);
        tvChild.setTextColor(Color.BLACK);
        tvChild.setTextSize(70);
        linear.addView(tvChild);

        setColors(position, linear);
        container.addView(linear);
        return linear;
    }

    public void setColors(int position, View layout){

        try {
            String colorString = "#FFF";
            layout.setBackgroundColor(Color.parseColor(colorString));
        } catch (Exception ex){
            Log.e("XXX", "Fail to load color ["+mParent+"]["+position+"]");
        }

    }

    public void loadJSONFromAsset(Context ctx) {
    }
}