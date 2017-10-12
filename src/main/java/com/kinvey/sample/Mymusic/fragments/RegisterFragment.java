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

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.sample.Mymusic.MainActivity;
import com.kinvey.sample.Mymusic.R;



public class RegisterFragment extends KinveyFragment implements View.OnClickListener {


    private EditText confirmPassword;
    private Button registerButton;
    private EditText userName;
    private EditText password;
    private EditText firstname;
    private EditText lastname;
    private EditText phonenumber;

    private TextView usernameLabel;
    private TextView passwordLabel;
    private TextView confirmPasswordLabel;

    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 5;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getViewID() {
        return R.layout.fragment_register;
    }

    @Override
    public void bindViews(View v) {
        registerButton = (Button) v.findViewById(R.id.register_create_account);
        userName = (EditText) v.findViewById(R.id.register_username);
        password = (EditText) v.findViewById(R.id.register_password);
        confirmPassword = (EditText) v.findViewById(R.id.register_confirm_password);
        firstname = (EditText) v.findViewById(R.id.firstname);
        lastname = (EditText) v.findViewById(R.id.lastname);
        phonenumber = (EditText) v.findViewById(R.id.phonenumber);
        /*usernameLabel = (TextView) v.findViewById(R.id.register_username_label);
        passwordLabel = (TextView) v.findViewById(R.id.register_password_label);
        confirmPasswordLabel = (TextView) v.findViewById(R.id.register_confirm_label);

        usernameLabel.setTypeface(getRoboto());
        passwordLabel.setTypeface(getRoboto());
        confirmPasswordLabel.setTypeface(getRoboto());*/

        this.addEditListeners();

    }


    private void addEditListeners() {

        registerButton.setOnClickListener(this);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                registerButton.setEnabled(validateInput());
            }
        });

        userName.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && userName.getText().length() < MIN_USERNAME_LENGTH
                                ) {

                            CharSequence text = "User name must contain at least " + MIN_USERNAME_LENGTH + " characters";
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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
                registerButton.setEnabled(validateInput());
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


        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                registerButton.setEnabled(validateInput());
                if(validateInput() == false){confirmPassword.setError("Not Match");}
            }
        });

        confirmPassword.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && (confirmPassword.getText().length() < MIN_USERNAME_LENGTH
                                || !password.getText().toString().equals(confirmPassword.getText().toString())
                        )) {
                            CharSequence text = "Repeat password must contain at least " + MIN_PASSWORD_LENGTH + " characters and equal password";
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });
    }

    private boolean validateInput() {
        return (userName.toString().length() >= MIN_USERNAME_LENGTH
                && password.getText().length() >= MIN_PASSWORD_LENGTH
                && confirmPassword.getText().length() >= MIN_PASSWORD_LENGTH
                && password.getText().toString().equals(confirmPassword.getText().toString()));
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void attemplogin(){
        // Reset errors.
        userName.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        firstname.setError(null);
        lastname.setError(null);
        phonenumber.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password.getText().toString()) && !isPasswordValid(password.getText().toString())) {
            password.setError("This password must more than four length");
            focusView = password;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(userName.getText().toString())) {
            userName.setError("This field is required");
            focusView = userName;
            cancel = true;
        } else if (!isEmailValid(userName.getText().toString())) {
            userName.setError("This email address is invalid");
            focusView = userName;
            cancel = true;
        }

        if(TextUtils.isEmpty(firstname.getText().toString())) {
            firstname.setError("This field is required");
            focusView = firstname;
            cancel = true;
        }

        if(TextUtils.isEmpty(lastname.getText().toString())) {
            lastname.setError("This field is required");
            focusView = lastname;
            cancel = true;
        }

        if(TextUtils.isEmpty(phonenumber.getText().toString())) {
            phonenumber.setError("This field is required");
            focusView = phonenumber;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            submit();
        }
    }
    @Override
    public void onClick(View v) {
        if (v == registerButton){
            attemplogin();

        }

    }

    KinveyClientCallback<User> callback = new KinveyUserCallback() {
        public void onFailure(Throwable t) {
            if (getActivity() == null){
                return;
            }
            CharSequence text = "Username already exists.";
            Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }

        public void onSuccess(User u) {
            getClient().user().put("First Name", firstname.getText().toString());
            getClient().user().put("Last Name", lastname.getText().toString());
            getClient().user().put("Phone Number", phonenumber.getText().toString());
            getClient().user().update(new KinveyUserCallback() {
                @Override
                public void onFailure(Throwable e) {
                    if (getActivity() == null) {
                        return;
                    }
                    Toast.makeText(getActivity(), "Failed to Register", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(User user) {
                    if (getActivity() == null) {
                        return;
                    }
                    Toast.makeText(getActivity(), "Welcome " + user.get("First Name"), Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).replaceFragment(new ShareListFragment(), false);
                }
            });
        }

    };


    public void submit() {
        getClient().user().create(userName.getText().toString(), password.getText().toString(), callback);
    }

}
