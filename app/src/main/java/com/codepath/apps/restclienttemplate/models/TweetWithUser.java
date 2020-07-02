package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    @Embedded(prefix = "tweet_")
    private Tweet tweet;

    @Embedded
    private User user;

    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets = new ArrayList<>();
        for (TweetWithUser tweetWithUser : tweetWithUsers) {
            Tweet currentTweet = tweetWithUser.tweet;
            User currentUser = tweetWithUser.user;
            currentTweet.setUser(currentUser);
            tweets.add(currentTweet);
        }
        return tweets;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
