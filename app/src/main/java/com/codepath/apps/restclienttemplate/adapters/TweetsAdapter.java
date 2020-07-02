package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.REST.TwitterApplication;
import com.codepath.apps.restclienttemplate.REST.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.databinding.LayoutTwitterButtonsBinding;
import com.codepath.apps.restclienttemplate.helpers.ParseRelativeDate;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    public static final String TAG = "TweetsAdapter";
    private Context context;
    private List<Tweet> tweets;
    private TwitterClient client;


    public TweetsAdapter(List<Tweet> tweets, Context context) {
        this.context = context;
        this.tweets = tweets;
        this.client = TwitterApplication.getRestClient(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tweets.get(position));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPoster;
        private ImageView ivMedia;
        private TextView tvName;
        private TextView tvBody;
        private TextView tvUsername;
        private TextView tvTimestamp;
        private ItemTweetBinding binding;
        private LayoutTwitterButtonsBinding buttonLayoutBinding;
        private ImageButton ibReply;
        private ImageButton ibRetweet;
        private ImageButton ibLike;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
            ivPoster = binding.ivProfile;
            ivMedia = binding.ivMedia;
            tvName = binding.tvName;
            tvBody = binding.tvBody;
            tvTimestamp = binding.tvTimestamp;
            tvUsername = binding.tvUsername;
            buttonLayoutBinding = binding.buttonHolderLayout;
            ibReply = buttonLayoutBinding.ibReply;
            ibRetweet = buttonLayoutBinding.ibRetweet;
            ibLike = buttonLayoutBinding.ibLike;
        }

        public void bind(Tweet tweet) {

            if (tweet.hasMediaUrl()) {
                Glide.with(context).load(tweet.getMediaUrl()).into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
            Glide.with(context).load(tweet.getUser().getPublicImageUrl()).into(ivPoster);
            tvName.setText(tweet.getUser().getName());
            tvBody.setText(tweet.getBody());
            tvUsername.setText("@" + tweet.getUser().getScreenName());
            tvTimestamp.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt()));
            if (tweet.isLiked()) {
                setToLiked();
            } else {
                setToNotLiked();
            }
            if (tweet.isRetweeted()) {
                setToRetweeted();
            } else {
                setToNotRetweeted();
            }

            handleButtons(tweet);
        }

        /**
         * Handles the on click listeners for each of the buttons
         * at the bottom of each tweet.
         */
        private void handleButtons(final Tweet tweet) {
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Reply button pressed for tweet at position " + getAdapterPosition(),
                            Toast.LENGTH_SHORT).show();
                }
            });

            ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tweet.isRetweeted()) {
                        client.unretweetTweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "Successfully unretweeted tweet at position " + getAdapterPosition());
                                tweet.toggleRetweeted();
                                notifyItemChanged(getAdapterPosition());
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "Failed to unretweet tweet at position " + getAdapterPosition());
                            }
                        });
                    } else {
                        client.retweetTweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "Successfully retweeted tweet at position " + getAdapterPosition());
                                setToRetweeted();
                                tweet.toggleRetweeted();
                                notifyItemChanged(getAdapterPosition());
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "Failed to retweet tweet at position " + getAdapterPosition());
                            }
                        });
                    }
                }
            });

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tweet.isLiked()) {
                        client.unlikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "Successfully unliked tweet at position " + getAdapterPosition());
                                try {
                                    Tweet response = Tweet.fromJson(json.jsonObject);
                                    tweets.set(getAdapterPosition(), response);
                                    notifyItemChanged(getAdapterPosition());
                                } catch (JSONException e) {
                                    Log.i(TAG, "Failed to read tweet from JSON response in UNLIKE.");
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "Failed to unlike tweet at position " + getAdapterPosition());
                            }
                        });
                    } else {
                        client.likeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "Successfully liked tweet at position " + getAdapterPosition());
                                try {
                                    Tweet response = Tweet.fromJson(json.jsonObject);
                                    tweets.set(getAdapterPosition(), response);
                                    notifyItemChanged(getAdapterPosition());
                                } catch (JSONException e) {
                                    Log.i(TAG, "Failed to read tweet from JSON response in LIKE.");
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "Failed to like tweet at position " + getAdapterPosition());
                            }
                        });
                    }
                }
            });
        }

        private void setToLiked() {
            ibLike.setImageResource(R.drawable.ic_vector_heart);
            ibLike.setColorFilter(ContextCompat.getColor(context, R.color.inline_action_like_pressed),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        private void setToNotLiked() {
            ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            ibLike.setColorFilter(ContextCompat.getColor(context, R.color.light_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        private void setToRetweeted() {
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            ibRetweet.setColorFilter(ContextCompat.getColor(context, R.color.twitter_blue_fill_pressed),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        private void setToNotRetweeted() {
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            ibRetweet.setColorFilter(ContextCompat.getColor(context, R.color.light_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

}
