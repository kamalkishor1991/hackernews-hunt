package app.techinshorts.techinshortsapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    RequestQueue mRequestQueue;
    Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into((ImageView) findViewById(R.id.image));
    final TextView textView = (TextView) findViewById(R.id.textview);
// Instantiate the cache
    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

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
            textView.setText(response.toString());
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
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
