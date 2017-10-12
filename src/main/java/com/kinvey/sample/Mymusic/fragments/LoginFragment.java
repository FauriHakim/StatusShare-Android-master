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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.sample.Mymusic.MainActivity;
import com.kinvey.sample.Mymusic.R;
import com.kinvey.sample.Mymusic.signingoogle.GoogleLoginActivity;


public class LoginFragment extends KinveyFragment implements View.OnClickListener{

    private EditText username;
    private EditText password;
    private Button login;
    private Button register;
    private ImageButton signgoogle;
    private TextView usernameLabel;
    private TextView passwordLabel;

    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MIN_PASSWORD_LENGTH = 4;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getClient().user().isUserLoggedIn()){
            Log.i(MainActivity.TAG, "logged in: " + getClient().user().getUsername());
            Toast.makeText(getActivity(), "Logging in", Toast.LENGTH_SHORT);
            loggedIn();

        }

    }



    @Override
    public int getViewID() {
        return R.layout.fragment_login;
    }

    @Override
    public void bindViews(View v) {
        username = (EditText) v.findViewById(R.id.et_login);
        password = (EditText) v.findViewById(R.id.et_password);

        /*usernameLabel = (TextView) v.findViewById(R.id.login_label_username);
        passwordLabel = (TextView) v.findViewById(R.id.login_label_password);*/

        login = (Button) v.findViewById(R.id.login);
        register = (Button) v.findViewById(R.id.register);
        signgoogle = (ImageButton) v.findViewById(R.id.LoginGoogle);

        //login.setTypeface(getRoboto());
        /*usernameLabel.setTypeface(getRoboto());
        passwordLabel.setTypeface(getRoboto());*/
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        signgoogle.setOnClickListener(this);
    }





    @Override
    public void onClick(View v) {
        if (v == login){
            getClient().user().login(username.getText().toString(), password.getText().toString(), new KinveyUserCallback() {
                @Override
                public void onSuccess(User result) {
                    if (getActivity() == null){
                        return;
                    }
                    CharSequence text = "Logged in " + result.get("First Name") + ".";
                    Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    loggedIn();
                }

                public void onFailure(Throwable t) {
                    if (getActivity() == null){
                        return;
                    }
                    CharSequence text = "Wrong username or password";
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            });


        }
        else if(v == register)
        {
            Register();
        }
        else
        {
            SigninGoogle();
        }
    }

    protected void addEditListeners() {
        login.setEnabled(validateInput());

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                login.setEnabled(validateInput());
            }
        });

        username.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && username.getText().length() < MIN_USERNAME_LENGTH
                                ) {

                            CharSequence text = "User name must contain at least " + MIN_USERNAME_LENGTH + " characters";
                            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                login.setEnabled(validateInput());
            }
        });

        password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && password.getText().length() < MIN_USERNAME_LENGTH
                                ) {
                            CharSequence text = "Password must contain at least " + MIN_PASSWORD_LENGTH + " characters";
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });



    }

    public boolean validateInput() {
        return (username.toString().length() >= MIN_USERNAME_LENGTH
                && password.getText().length() >= MIN_PASSWORD_LENGTH
        );
    }

    private void loggedIn(){

        if (getActivity() != null && getActivity().getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        ((MainActivity) getActivity()).replaceFragment(new ShareListFragment(), false);


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_login, menu);
    }

    public void Register(){
        ((MainActivity) getActivity()).replaceFragment(new RegisterFragment(), true);
    }

    public void SigninGoogle(){
        Intent intent = new Intent(getActivity(), GoogleLoginActivity.class);
        getActivity().startActivity(intent);
    }
}


