package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.apis.TwitterApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";

    private TwitterClient client;
    private List<Tweet> tweets;

    private RecyclerView rvTimeline;
    private TweetsAdapter adapter;

    public static final int REQUEST_POST_TWEET = 100;

    public static final String KEY_TWEET = "tweet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        tweets = new ArrayList<>();
        client = TwitterApplication.getRestClient(this);
        setupRV();
        populateHomeTimeline();

    }

    private void setupRV() {
        rvTimeline = findViewById(R.id.rvTimeline);
        adapter = new TweetsAdapter(tweets, this);
        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        rvTimeline.setAdapter(adapter);
    }

    // Executes the API request and populates the recycler view.
    private void populateHomeTimeline() {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success on retrieving tweets.");
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "Success on parsing tweets.");
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to retrieve tweets: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to retrieve tweets.");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.miCompose) {
            Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
            startActivityForResult(i, REQUEST_POST_TWEET);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == REQUEST_POST_TWEET && requestCode == RESULT_OK) {
            Log.i(TAG, "Received data from child activity");
            Tweet tweet = data.getParcelableExtra(KEY_TWEET);
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTimeline.smoothScrollToPosition(0);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}