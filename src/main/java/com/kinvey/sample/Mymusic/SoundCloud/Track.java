package com.kinvey.sample.Mymusic.SoundCloud;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fauri hakim on 08/06/2016.
 */

public class Track {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("id")
    private int mID;

    @SerializedName("stream_url")
    private String mStreamURL;

    @SerializedName("artwork_url")
    private String mArtworkURL;

    public String getTitle() {
        return mTitle;
    }

    public int getID() {
        return mID;
    }

    public String getStreamURL() {
        return mStreamURL;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }
}
