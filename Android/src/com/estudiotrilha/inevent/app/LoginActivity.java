package com.estudiotrilha.inevent.app;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.estudiotrilha.android.utils.FormUtils;
import com.estudiotrilha.inevent.InEvent;
import com.estudiotrilha.inevent.R;
import com.estudiotrilha.inevent.content.ApiRequest;
import com.estudiotrilha.inevent.content.Event;
import com.estudiotrilha.inevent.content.LoginManager;
import com.estudiotrilha.inevent.content.Member;
import com.estudiotrilha.inevent.provider.InEventProvider;
import com.google.analytics.tracking.android.EasyTracker;


public class LoginActivity extends ActionBarActivity
{
    // App State
    private static final String STATE_USERNAME = LoginActivity.class.getName()+"state.USERNAME";

    // Api Request Code
    private static final int REQUEST_LOGIN = 1;


    private EditText mEmail;
    private EditText mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Don't show the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // setup the button functions
        findViewById(R.id.login_confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (FormUtils.isEmailValid(mEmail.getText().toString()))
                {
                    setUserInteractionEnabled(false);
                    loginAttempt();
                }
                else
                {
                    mEmail.setError(getText(R.string.error_invalidEmail));
                }
            }
        });


        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);

        if (savedInstanceState == null)
        {
            // recover last logged in username
            String username = PreferenceManager.getDefaultSharedPreferences(this).getString(STATE_USERNAME, "");
            mEmail.setText(username);
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        if (!InEvent.DEBUG)
        {
            EasyTracker.getInstance().activityStart(this);
        }
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        if (!InEvent.DEBUG)
        {
            EasyTracker.getInstance().activityStop(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
        case android.R.id.home:
            finish();
            break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loginAttempt()
    {
        // Shows a loading progress
//        final DialogFragment progress = ProgressDialogFragment.createBuilder(this, getSupportFragmentManager()) // XXX
//                .setMessage(R.string.message_loggingIn)
//                .setCancelable(false)
//                .show();
        final Dialog progress = ProgressDialog.show(this, null, getText(R.string.message_loggingIn), true, false);

        try
        {
            HttpURLConnection connection;

            final String memberName = mEmail.getText().toString();
            final String password   = mPassword.getText().toString();

            // Send the API request
            connection = Member.Api.signIn(memberName, password);
            ApiRequest.getJsonFromConnection(REQUEST_LOGIN, connection, new ApiRequest.ResponseHandler() {
                @Override
                public void handleResponse(int requestCode, JSONObject json, int responseCode)
                {
                    if (responseCode != HttpStatus.SC_OK || json == null)
                    {
                        // Treat a bad response code
                        int errorConnection = R.string.error_connection;

                        switch (responseCode)
                        {
                        // TODO treat other the responses
                        case HttpStatus.SC_UNAUTHORIZED:
                            // Bad credentials
                            errorConnection = R.string.error_login_badCredentials;
                            break;

                        case HttpStatus.SC_REQUEST_TIMEOUT:
                            // Time out
                            errorConnection = R.string.error_connection_timeout;
                            break;
                        }

                        Toast.makeText(LoginActivity.this, errorConnection, Toast.LENGTH_SHORT).show();
                    }
                    else if (LoginManager.getInstance(LoginActivity.this).signIn(Member.fromJson(json)))
                    {
                        // close the LoginActivity
                        finish();
                        // save the username
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                                .putString(STATE_USERNAME, mEmail.getText().toString())
                                .commit();

                        // Get the events
                        getEvents(json);
                    }
                    else
                    {
                        // Notify the user about some internal error
                        Toast.makeText(LoginActivity.this, R.string.error_internal, Toast.LENGTH_SHORT).show();
                    }

                    if (progress != null) progress.dismiss(); // XXX
                    setUserInteractionEnabled(true);
                }
            });
        }
        catch (IOException e)
        {
            if (progress != null) progress.dismiss(); // XXX
            setUserInteractionEnabled(true);

            Log.e(InEvent.NAME, "Couldn't create a connection for login", e);
        }
    }
    private void getEvents(final JSONObject json)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                
                try
                {
                    // Delete the previous events
                    operations.add(
                            ContentProviderOperation
                                .newDelete(Event.EVENT_CONTENT_URI)
                                .build()
                    );

                    // Get the new ones
                    JSONArray eventArray = json.getJSONArray("events");
                    for (int i = 0; i < eventArray.length(); i++)
                    {
                        // Parse the json
                        ContentValues values = Event.valuesFromJson(eventArray.getJSONObject(i));

                        // Add the insert operation
                        operations.add(
                                ContentProviderOperation
                                    .newInsert(Event.EVENT_CONTENT_URI)
                                    .withValues(values)
                                    .build()
                        );
                    }

                    getContentResolver().applyBatch(InEventProvider.AUTHORITY, operations);
                }
                catch (JSONException e)
                {
                    Log.w(InEvent.NAME, "Couldn't properly get the Events from the json = "+json, e);
                }
                catch (RemoteException e)
                {
                    Log.e(InEvent.NAME, "", e);
                }
                catch (OperationApplicationException e)
                {
                    Log.e(InEvent.NAME, "Failed while adding Events to the database", e);
                }
            }
        });

        thread.start();
    }


    private void setUserInteractionEnabled(boolean enabled)
    {
        // the text fields
        mEmail.setEnabled(enabled);
        mPassword.setEnabled(enabled);

        // the buttons
        findViewById(R.id.login_confirmButton).setEnabled(enabled);
    }
}