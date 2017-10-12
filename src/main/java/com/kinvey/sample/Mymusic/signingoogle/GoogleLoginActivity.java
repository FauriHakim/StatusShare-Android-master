/*
 * Copyright (c) 2014 Kinvey Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kinvey.sample.Mymusic.signingoogle;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.sample.Mymusic.R;
import com.kinvey.sample.Mymusic.StatusShareApplication;
import com.kinvey.sample.Mymusic.fragments.ShareListFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class GoogleLoginActivity extends AuthorizedActivity{

    /** OAuth 2.0 scope for writing a moment to the user's Google+ history. */
    static final String SCOPE_STRING = "oauth2:https://www.googleapis.com/auth/plus.me";
    private static final String PLUS_PEOPLE_ME = "https://www.googleapis.com/plus/v1/people/me";

    private static final int GPLAY_REQUEST_CODE = 782049854;

    private String mID;
    private String mAccount;
    private AccountManager mAccountManager;

    private AppCompatActivity act;


    private static final String PARAM_LOGIN_TYPE_GOOGLE = "google";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getClient().user().isUserLoggedIn()){
            toMainActivity();
        }
        setContentView(R.layout.google_login);
        act = new AppCompatActivity();
        mAccountManager = AccountManager.get(this);
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, GPLAY_REQUEST_CODE);
    }

    private void toMainActivity() {
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        getClient().user().logout().execute();
    }

    private String getAccountName() {

        String accountName = null;

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account account : list) {
            if (account.type.equalsIgnoreCase("com.google")) {
                accountName = account.name;
                break;
            }
        }
        return accountName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPLAY_REQUEST_CODE && resultCode == RESULT_OK) {
            mAccount = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            get(PLUS_PEOPLE_ME, mAccount, SCOPE_STRING, null,
                    new ResponseHandler() {

                        @Override
                        public void handle(Response response) {
                            if (response.status != 200) {
                                error(response);
                                return;
                            }
                            try {
                                JSONObject json = new JSONObject(new String(
                                        response.body));
                                mID = json.optString("id");

                                loginGoogleKinveyUser();

                            } catch (JSONException je) {
                                throw new RuntimeException(je);
                            }
                        }

                    });
        }
    }

    private void loginGoogleKinveyUser() {

        getClient().user().loginGoogle(getAuthToken(),
                new KinveyUserCallback() {

                    public void onFailure(Throwable e) {
                        Log.e(TAG, "Failed Kinvey login", e);
                        // TextView tv = (TextView) findViewById(R.id.output);
                        String b = new String(e.getMessage());
                        Log.e(AuthorizedActivity.TAG, "Error: " + b);
                        // tv.setText("DOH!  Great Scott!\nKinvey: " + b);
                    }

                    ;

                    @Override
                    public void onSuccess(User r) {
                        String email = getAccountName();
                        CharSequence text = "Logged in. ";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                        if (getClient().user().isUserLoggedIn()) {
                            //loggedin();
                        }
                        GoogleLoginActivity.this.finish();

                    }
                });
    }

    public void loggedin(){
        replaceFragment(new ShareListFragment(), false);
    }

    public void replaceFragment(Fragment frag, boolean addToBackStack) {
        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentBox, frag);
        if (addToBackStack) {
            ft.addToBackStack(frag.toString());
        }
        ft.commit();
    }

    private GoogleApiClient mGoogleApiClient;
    protected void error(Response response) {
        // TextView tv = (TextView) findViewById(R.id.output);
        String b = new String(response.body);
        Log.d(AuthorizedActivity.TAG, "Error " + response.status + " body: "
                + b);
        // tv.setText("OUCH!  The Internet never works!\n" + b);
    }

    public Client getClient(){
        return ((StatusShareApplication)getApplication()).getClient();
    }

}