package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet implements Parcelable {

    private String body;
    private String createdAt;
    private String mediaUrl;
    private User user;

    public static final String MISSING_URL_FLAG = "NO MEDIA FOUND";


    protected Tweet(Parcel in) {
        body = in.readString();
        createdAt = in.readString();
        mediaUrl = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    private Tweet() {
    }

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

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.mediaUrl = MISSING_URL_FLAG;
        tweet.setMediaUrl(jsonObject);
        return tweet;
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

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public boolean hasMediaUrl() {
        return !mediaUrl.equals(MISSING_URL_FLAG);
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
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
        parcel.writeParcelable(user, flags);
    }
}
