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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.LinkedResources.LinkedFile;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyMetaData;
import com.kinvey.sample.Mymusic.MainActivity;
import com.kinvey.sample.Mymusic.R;
import com.kinvey.sample.Mymusic.model.UpdateEntity;

import java.io.ByteArrayInputStream;


public class ProfileFragment extends KinveyFragment implements View.OnClickListener {

    private Button saveButton;
    private EditText username;
    private EditText password;
    private EditText firstname;
    private EditText lastname;
    private EditText phonenumber;
    private EditText updateText;
    private TextView title;
    private ImageView foto;

    private UpdateEntity entity;

    private TextView usernameLabel;
    private TextView passwordLabel;
    private TextView confirmPasswordLabel;

    private ImageView attachmentImage;
    private AlertDialog mDialog;
    private TextView attachmentTitle;
    private Bitmap image;

    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 5;

    public UpdateEntity getEntity() {
        return entity;
    }

    public void setEntity(UpdateEntity entity) {
        this.entity = entity;
    }

    public static ProfileFragment newInstance(UpdateEntity entity){
        ProfileFragment frag = new ProfileFragment();
        frag.setEntity(entity);
        frag.setHasOptionsMenu(true);


        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setvalue();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.image = ((MainActivity) getActivity()).bitmap;


        if (this.image != null) {
            Log.i(Client.TAG, "setting imageview");
            attachmentImage.setBackgroundDrawable(null);
            attachmentImage.setImageBitmap(this.image);
        } else {

            Log.i(Client.TAG, "not setting imageview");
        }
    }
    public void setvalue() {
        username.append(getClient().user().get("username").toString());
        username.setEnabled(false);
        password.setEnabled(false);
        firstname.append(getClient().user().get("First Name").toString());
        lastname.append(getClient().user().get("Last Name").toString());
        phonenumber.append(getClient().user().get("Phone Number").toString());
        password.append("**********");
    }

            @Override
            public int getViewID() {
                return R.layout.profile_fragment;
            }

            @Override
            public void bindViews(View v) {
                saveButton = (Button) v.findViewById(R.id.profile_save);
                username = (EditText) v.findViewById(R.id.profile_username);
                password = (EditText) v.findViewById(R.id.profile_password);
                firstname = (EditText) v.findViewById(R.id.profile_firstname);
                lastname = (EditText) v.findViewById(R.id.profile_lastname);
                phonenumber = (EditText) v.findViewById(R.id.profile_phonenumber);
                setvalue();
        /*usernameLabel = (TextView) v.findViewById(R.id.register_username_label);
        passwordLabel = (TextView) v.findViewById(R.id.register_password_label);
        confirmPasswordLabel = (TextView) v.findViewById(R.id.register_confirm_label);

        usernameLabel.setTypeface(getRoboto());
        passwordLabel.setTypeface(getRoboto());
        confirmPasswordLabel.setTypeface(getRoboto());*/

                this.addEditListeners();

            }


            private void addEditListeners() {

                saveButton.setOnClickListener(this);

                username.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        saveButton.setEnabled(validateInput());
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
                        saveButton.setEnabled(validateInput());
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

            private boolean validateInput() {
                return (username.toString().length() >= MIN_USERNAME_LENGTH
                        && password.getText().length() >= MIN_PASSWORD_LENGTH);
            }

            private boolean isEmailValid(String email) {
                //TODO: Replace this with your own logic
                return email.contains("@");
            }

            private boolean isPasswordValid(String password) {
                //TODO: Replace this with your own logic
                return password.length() > 4;
            }

            private void attemplogin() {
                // Reset errors.
                username.setError(null);
                password.setError(null);
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
                if (TextUtils.isEmpty(username.getText().toString())) {
                    username.setError("This field is required");
                    focusView = username;
                    cancel = true;
                } else if (!isEmailValid(username.getText().toString())) {
                    username.setError("This email address is invalid");
                    focusView = username;
                    cancel = true;
                }

                if (TextUtils.isEmpty(firstname.getText().toString())) {
                    firstname.setError("This field is required");
                    focusView = firstname;
                    cancel = true;
                }

                if (TextUtils.isEmpty(lastname.getText().toString())) {
                    lastname.setError("This field is required");
                    focusView = lastname;
                    cancel = true;
                }

                if (TextUtils.isEmpty(phonenumber.getText().toString())) {
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
                if (v == saveButton) {
                    attemplogin();

                }
                if (v == attachmentImage) {
                    mDialog.show();

                }

            }

            public void submit() {
                getClient().user().put("First Name", firstname.getText().toString());
                getClient().user().put("Last Name", lastname.getText().toString());
                getClient().user().put("Phone Number", phonenumber.getText().toString());
                getClient().user().update(new KinveyUserCallback() {
                    @Override
                    public void onFailure(Throwable e) {
                        if (getActivity() == null) {
                            return;
                        }
                        Toast.makeText(getActivity(), "Failed to Updated Profile", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(User user) {
                        if (getActivity() == null) {
                            return;
                        }
                        Toast.makeText(getActivity(), "Successfully Updated Profile ", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).replaceFragment(new ProfileFragment(), false);
                    }
                });
            }

    public void saveUpdateAttachment(final ProgressDialog progressDialog, byte[] bytes, String filename) {
        UpdateEntity updateEntity = new UpdateEntity(getClient().user().getId());
        //updateEntity.setText(updateText.getText().toString());
        updateEntity.getAcl().setGloballyReadable(true);

        android.util.Log.d(Client.TAG, "updateEntity.getMeta().isGloballyReadable() = " + updateEntity.getAcl().isGloballyReadable());

        if (bytes != null && filename != null) {
            Log.i(Client.TAG, "there is an attachment!");
            LinkedFile lf = new LinkedFile(filename);
            lf.addExtra("_public", true);
            KinveyMetaData.AccessControlList acl = new KinveyMetaData.AccessControlList();
            acl.setGloballyReadable(true);
            lf.addExtra("_acl", acl);
            updateEntity.putFile("attachment", lf);
        }
        final ByteArrayInputStream bais = ((bytes == null) ? null : new ByteArrayInputStream(bytes));
        if (bais != null){
            updateEntity.getFile("attachment").setInput(bais);
        }
        getClient().linkedData(MainActivity.COL_UPDATES, UpdateEntity.class).save(updateEntity, new KinveyClientCallback<UpdateEntity>() {

                    @Override
                    public void onSuccess(UpdateEntity result) {
                        if (getActivity() == null) {
                            return;
                        }
                        android.util.Log.d(Client.TAG, "postUpdate: SUCCESS _id = " + result.getId() + ", gr = " + result.getAcl().isGloballyReadable());
                        progressDialog.dismiss();

                        try {
                            bais.close();
                        } catch (Exception e) {
                        }

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(updateText.getWindowToken(), 0);

                        ((MainActivity) getActivity()).bitmap = null;

                        if (getActivity() != null) {
                            ((MainActivity) getActivity()).setShareList(null);

                            ((MainActivity) getActivity()).replaceFragment(new ProfileFragment(), false);

//                    ((StatusShare)getSherlockActivity()).removeFragment(UpdateEditFragment.this);
//                    ((StatusShare)((StatusShare) getSherlockActivity()).removeFragment(getSherlockActivity().getSupportFragmentManager().);)
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        if (getActivity() == null) {
                            return;
                        }
                        Log.d(Client.TAG, "failed to update your photo profile");
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }, null
        );
//        } else {
//            Log.i(Client.TAG, "there is no attachment");
//        }
    }

    /*@Override
    public void populateViews() {
        if (entity.getThumbnail() != null) {
            attachmentImage.setImageBitmap(entity.getThumbnail());
        } else {
            attachmentImage.setVisibility(View.GONE);
        }
    }*/

        }
