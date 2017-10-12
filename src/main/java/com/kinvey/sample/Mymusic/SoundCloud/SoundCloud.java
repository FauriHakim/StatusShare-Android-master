package com.kinvey.sample.Mymusic.SoundCloud;

import retrofit.RestAdapter;

/**
 * Created by Fauri hakim on 08/06/2016.
 */
public class SoundCloud {
    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder().setEndpoint(Config.API_URL).build();
    private static final SCService SERVICE = REST_ADAPTER.create(SCService.class);

    public static SCService getService() {
        return SERVICE;
    }
}
