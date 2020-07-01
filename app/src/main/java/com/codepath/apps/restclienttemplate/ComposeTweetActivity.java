package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

/**
 * Handles the activity responsible for making and
 * posting tweets to Twitter.
 */
public class ComposeTweetActivity extends AppCompatActivity {

    private static final String TAG = "ComposeTweetActivity";
    private TextView etBody;
    private Button btSubmit;

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        client = TwitterApplication.getRestClient(this);

        bindElements();
        setupPostButton();



    }


    /**
     * Locates and assigns the activity elements.
     * todo Switch to ViewBinding
     */
    private void bindElements() {
        etBody = findViewById(R.id.etBody);
        btSubmit = findViewById(R.id.btSubmit);
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
                } else if (tweetBody.length() > 140) {
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
     * @param tweetBody The text contained in the
     *                  body of the tweet ot be
     *                  posted.
     */
    private void postTweet(String tweetBody) {
        client.postTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Successfully posted tweet");
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


}