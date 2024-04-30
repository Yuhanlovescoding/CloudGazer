package com.example.cloudgazer;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.content.Intent;
import android.os.Looper;

/**
 * SplashActivity displays a brief introductory or loading screen when the app starts.
 * It automatically transitions to the MainActivity after a set delay, providing a user-friendly
 * introduction to the app.
 */
public class SplashActivity extends AppCompatActivity {

    // Duration in milliseconds to show the splash screen before transitioning to the main activity
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)to
     * programmatically interact with widgets in the UI, etc.
     *
     * @param savedInstanceState if the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setting the content view to the splash screen layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Create a Handler attached to the main thread
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start the MainActivity
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                // Start MainActivity
                SplashActivity.this.startActivity(mainIntent);
                // Close the SplashActivity
                SplashActivity.this.finish();
            }
            // Delay configured by SPLASH_DISPLAY_LENGTH
        }, SPLASH_DISPLAY_LENGTH);
    }
}