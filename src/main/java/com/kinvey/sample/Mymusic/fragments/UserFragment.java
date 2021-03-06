/** 
 * Copyright (c) 2014 Kinvey Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package com.kinvey.sample.Mymusic.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.query.AbstractQuery;
import com.kinvey.sample.Mymusic.R;
import com.kinvey.sample.Mymusic.MainActivity;
import com.kinvey.sample.Mymusic.component.UpdateAdapter;
import com.kinvey.sample.Mymusic.model.UpdateEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserFragment extends KinveyFragment {

    public static final Integer UPDATES_LIST_SIZE = 5;


    private List<UpdateEntity> updates;

    private ImageView avatar;
    private TextView author;
    private ListView lv;
    private TextView history_title;
    private ProgressBar loading;

    private UpdateEntity source;
    private Bitmap gravatar;

    public static UserFragment newInstance(UpdateEntity update) {
        UserFragment ret = new UserFragment();
        ret.setSource(update);
        ret.setHasOptionsMenu(true);
        return ret;
    }

    private UserFragment() {
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_view_author;
    }

    @Override
    public void bindViews(View v) {
        avatar = (ImageView) v.findViewById(R.id.avatar);
        author = (TextView) v.findViewById(R.id.author_name);
        history_title = (TextView) v.findViewById(R.id.auther_updates_title);
        lv = (ListView) v.findViewById(R.id.author_updateList);
        loading = (ProgressBar) v.findViewById(R.id.author_list_loading);

        author.setTypeface(getRoboto());
        history_title.setTypeface(getRoboto());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void populateViews(){
        if (this.source == null){
            return;
        }
        author.setText(this.source.getAuthorName());
        setAvatar(this.source.getAuthorName());


        if (lv.getAdapter() == null || lv.getAdapter().getCount() == 0){
           updateList();

        }




    }

    public void updateList() {

        loading.setVisibility(View.VISIBLE);
        lv.setVisibility(View.GONE);
        Query q = new Query();
        q.equals("_acl.creator", source.getAuthorID());
        q.addSort("_kmd.lmt", AbstractQuery.SortOrder.DESC);
        q.setLimit(UPDATES_LIST_SIZE);


        getClient().linkedData(MainActivity.COL_UPDATES, UpdateEntity.class).get(q, new KinveyListCallback<UpdateEntity>() {
            @Override
            public void onSuccess(UpdateEntity[] result) {
                android.util.Log.d(Client.TAG, "Count of updates found: " + result.length);

                if (    getActivity() == null) {
                    return;
                }

                for (UpdateEntity e : result) {
                    Log.d(Client.TAG, "result -> " + e.toString());
                    Log.d(Client.TAG, "attachment? -> " + (e.getFile(UpdateEntity.attachmentName) == null));

                    if (e.getFile(UpdateEntity.attachmentName) != null) {
                        Log.d(Client.TAG, "outputstream ? -> " + (e.getFile(UpdateEntity.attachmentName).getOutput() == null));
                    }
                }


                updates = new ArrayList<UpdateEntity>();
                updates.addAll(Arrays.asList(result));

                lv.setAdapter(new UpdateAdapter(getActivity(), updates, getActivity().getLayoutInflater()));
                loading.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);


            }


            @Override
            public void onFailure(Throwable error) {
                Log.w(Client.TAG, "Error fetching updates data: " + error.getMessage());
            }
        }, null, new String[]{"author"}, 1, true);

    }

    public void setSource(UpdateEntity ent){
        this.source = ent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }



    public void setAvatar(String gravatarID) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] digest = digester.digest(gravatarID.getBytes());

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }

            String url = new String("http://www.gravatar.com/avatar/" + sb.toString() + ".jpg?d=identicon");
            //android.util.Log.d(TAG, gravatarID + " = " + url);
            new DownloadAvatarTask().execute(url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private class DownloadAvatarTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                gravatar = BitmapFactory.decodeStream((InputStream) new URL(
                        params[0]).getContent());
            } catch (MalformedURLException e) {
                Log.e(Client.TAG, "url for avatar download is bad", e);
            } catch (IOException e) {
                Log.e(Client.TAG, "failed to download avatar", e);
            }



            return gravatar;
        }

        @Override
        protected void onPostExecute(Bitmap grav){

            if (gravatar != null){
                avatar.setImageBitmap(gravatar);
            }
        }

    }

}
