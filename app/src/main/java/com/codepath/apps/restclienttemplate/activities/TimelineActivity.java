package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.REST.TwitterApplication;
import com.codepath.apps.restclienttemplate.REST.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_POST_TWEET = 100;
    public static final String KEY_TWEET = "TWEET";
    private RelativeLayout rootLayout;
    private TwitterClient client;
    private TweetDao tweetDao;
    private List<Tweet> tweets;
    private RecyclerView rvTimeline;
    private TweetsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private MenuItem miActionProgressItem;
    private ActivityTimelineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        client = TwitterApplication.getRestClient(this);
        tweetDao = ((TwitterApplication) getApplicationContext()).getMyDatabase().tweetDao();

        bindElements();
        setupElements();
    }

    /**
     * Locates and assigns the activity elements.
     * todo Switch to ViewBinding
     */
    private void bindElements() {
        swipeContainer = binding.swipeContainer;
        rvTimeline = binding.rvTimeline;
        rootLayout = binding.rootLayout;
    }

    /**
     * Setup for all views.
     */
    private void setupElements() {
        setupRV();
        setupSwipeContainer();
    }

    /**
     * Handles the creation of the adapter and layout manager
     * for the Tweet Recycler View.
     */
    private void setupRV() {
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(tweets, this);
        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        rvTimeline.setAdapter(adapter);
    }

    /**
     * Setups the refresh on swipe functionality.
     */
    private void setupSwipeContainer() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * Loads tweets into the adapter asynchronously.
     */
    private void fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        showProgressBar();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                try {
                    List<Tweet> fromNetwork = Tweet.fromJsonArray(json.jsonArray);
                    adapter.addAll(fromNetwork);
                    saveToDB(fromNetwork);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Fetch timeline error: ", throwable);
                Snackbar.make(rootLayout.getRootView(), "Failed to load new tweets. " +
                        "Pulling recent tweets from memory...", Snackbar.LENGTH_SHORT).show();
                loadFromDB();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        fetchTimelineAsync();
        return super.onPrepareOptionsMenu(menu);
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

        if (requestCode == REQUEST_POST_TWEET && resultCode == RESULT_OK) {
            Log.i(TAG, "Received data from child activity");
            Tweet tweet = data.getParcelableExtra(KEY_TWEET);
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTimeline.smoothScrollToPosition(0);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    private void loadFromDB() {
        showProgressBar();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Loading tweets from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.getRecent();
                final List<Tweet> tweets = TweetWithUser.getTweetList(tweetWithUsers);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        adapter.addAll(tweets);
                        hideProgressBar();
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void saveToDB(final List<Tweet> tweetList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Saving tweets to database");
                tweetDao.insertModel(User.fromJsonTweetArray(tweetList).toArray(new User[0]));
                tweetDao.insertModel(tweetList.toArray(new Tweet[0]));
            }
        });
    }


}