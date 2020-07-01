package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.REST.TwitterApplication;
import com.codepath.apps.restclienttemplate.REST.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeTweetBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

/**
 * Handles the activity responsible for making and
 * posting tweets to Twitter.
 */
public class ComposeTweetActivity extends AppCompatActivity {

    private static final String TAG = "ComposeTweetActivity";
    // Maximum characters in a tweet.
    public static final int MAX_TWEET_LENGTH = 280;


    private TextInputLayout tlCounter;
    private TextView etBody;
    private Button btSubmit;
    private MenuItem miActionProgressItem;
    private ActivityComposeTweetBinding binding;

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComposeTweetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setContentView(R.layout.activity_compose_tweet);

        client = TwitterApplication.getRestClient(this);

        bindElements();
        setupElements();
    }


    /**
     * Locates and assigns the activity elements.
     * todo Switch to ViewBinding
     */
    private void bindElements() {
        etBody = binding.etBody;
        btSubmit = binding.btSubmit;
        tlCounter = binding.tlCounter;
    }

    /**
     * Setup for all views.
     */
    private void setupElements() {
        setupPostButton();
        setupTextLayout();
    }

    /**
     * Sets the maximum value for the character counter.
     */
    private void setupTextLayout() {
        tlCounter.setCounterMaxLength(MAX_TWEET_LENGTH);
    }


    /**
     * Prepares the OnClickListener for the post button.
     */
    private void setupPostButton() {
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetBody = etBody.getText().toString();
                if (tweetBody.length() == 0) {
                    Toast.makeText(ComposeTweetActivity.this,
                            "Sorry, your tweet cannot be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (tweetBody.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeTweetActivity.this,
                            "Sorry, your tweet is too long",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Posting Tweet...");
                    postTweet(tweetBody);
                }
            }
        });
    }

    /**
     * Handles the API request for posting a tweet.
     * Also handles the displaying and hiding of
     * the progress indicator while the post is
     * being made.
     * @param tweetBody The text contained in the
     *                  body of the tweet ot be
     *                  posted.
     */
    private void postTweet(String tweetBody) {
        showProgressBar();
        client.postTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Successfully posted tweet");
                hideProgressBar();
                sendBackTweet(json);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to post tweet", throwable);
            }
        });
    }

    /**
     * Attempts to extract the tweet from a json response,
     * and then passes it back to the parent activity.
     * @param tweetJson A json response containing the data for a tweet.
     */
    private void sendBackTweet(JsonHttpResponseHandler.JSON tweetJson) {
        JSONObject jsonObject = tweetJson.jsonObject;
        try {
            Tweet tweet = Tweet.fromJson(jsonObject);
            Intent result = new Intent();
            result.putExtra(TimelineActivity.KEY_TWEET, tweet);
            setResult(RESULT_OK, result);
            finish();
        } catch (JSONException e) {
            Log.e(TAG, "Failed to extract tweet from JSON");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }


}