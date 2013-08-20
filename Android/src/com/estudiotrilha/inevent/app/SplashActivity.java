package com.estudiotrilha.inevent.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.estudiotrilha.inevent.InEvent;
import com.estudiotrilha.inevent.R;
import com.estudiotrilha.inevent.content.LoginManager;
import com.google.analytics.tracking.android.EasyTracker;


public class SplashActivity extends Activity implements Runnable
{
    private static int SPLASH_DURATION = 800; // in milliseconds

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (!InEvent.DEBUG)
        {
            EasyTracker.getInstance().activityStart(this);
        }

        // set the transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Obtain the sharedPreference
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // default to true if not available
        boolean splashEnabled = sp.getBoolean(PreferencesActivity.SPLASH_ENABLED, true);

        if (splashEnabled)
        {
            // TODO Start the animation
            mHandler.postDelayed(this, SPLASH_DURATION);
        }
        else
        {
            onSplashFinish();
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

        // if stopped by the user, it won't open the other activity
        mHandler.removeCallbacks(this);
    }


    private void onSplashFinish()
    {
        // finish the splash activity
        finish();

        // set the transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Open the next Activity
        startActivity(new Intent(this, EventActivity.class));

        // If the user is not signIn yet
        if (!LoginManager.getInstance(this).isSignedIn())
        {
            // Open the login Screen
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    @Override
    public void run()
    {
        onSplashFinish();
    }
}