package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import okhttp3.Headers;

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

    private void bindElements() {
        etBody = findViewById(R.id.etBody);
        btSubmit = findViewById(R.id.btSubmit);
    }



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
                    client.postTweet(tweetBody, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Successfully posted tweet");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Failed to post tweet");
                        }
                    });
                }


            }
        });
    }

    private void postTweet(View view) {
        String tweetContent = etBody.getText().toString();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}