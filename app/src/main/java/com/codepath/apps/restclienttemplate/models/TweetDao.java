package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt," +
            "Tweet.mediaUrl AS tweet_mediaUrl, Tweet.liked AS tweet_liked, Tweet.retweeted AS" +
            "tweet_retweeted, User.* FROM Tweet INNER JOIN User ON Tweet.userId = User.id ORDER BY Tweet.id DESC LIMIT 5")
    public List<TweetWithUser> getRecent();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);


}

