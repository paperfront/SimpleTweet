package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeTweetActivity extends AppCompatActivity {

    private TextView etBody;
    private Button btSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

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
                }

                hideKeyboard();
                Toast.makeText(ComposeTweetActivity.this, tweetBody, Toast.LENGTH_SHORT).show();
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