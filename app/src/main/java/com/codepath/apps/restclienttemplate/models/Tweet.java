package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet implements Parcelable {

    public static final String MISSING_URL_FLAG = "NO MEDIA FOUND";
    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
    @ColumnInfo
    private String body;
    @ColumnInfo
    private String createdAt;
    @ColumnInfo
    private String mediaUrl;
    @ColumnInfo
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo
    private boolean liked;
    @ColumnInfo
    private boolean retweeted;
    @Ignore
    private User user;
    @ColumnInfo
    private long userId;


    protected Tweet(Parcel in) {
        body = in.readString();
        createdAt = in.readString();
        mediaUrl = in.readString();
        id = in.readString();
        liked = in.readBoolean();
        retweeted = in.readBoolean();
        user = in.readParcelable(User.class.getClassLoader());
        userId = in.readLong();
    }

    public Tweet() {

    }


    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.liked = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.id = jsonObject.getString("id_str");
        tweet.mediaUrl = MISSING_URL_FLAG;
        tweet.setMediaUrl(jsonObject);
        tweet.userId = tweet.user.getId();
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    private void setMediaUrl(JSONObject jsonObject) throws JSONException {
        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities.has("media")) {
            JSONArray mediaArray = entities.getJSONArray("media");
            JSONObject firstMedia = mediaArray.getJSONObject(0);
            mediaUrl = firstMedia.getString("media_url_https");
        } else {
            mediaUrl = MISSING_URL_FLAG;
        }
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public boolean hasMediaUrl() {
        return !mediaUrl.equals(MISSING_URL_FLAG);
    }

    public void toggleRetweeted() {
        retweeted = !retweeted;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(body);
        parcel.writeString(createdAt);
        parcel.writeString(mediaUrl);
        parcel.writeString(id);
        parcel.writeBoolean(liked);
        parcel.writeBoolean(retweeted);
        parcel.writeParcelable(user, flags);
        parcel.writeLong(userId);
    }
}


