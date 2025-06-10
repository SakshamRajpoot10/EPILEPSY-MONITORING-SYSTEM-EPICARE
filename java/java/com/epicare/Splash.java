package com.epicare;

import static com.epicare.utils.MyConstants.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    private static final int SPLASH_DELAY = 1000; // 1 second delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(() -> {
            SharedPreferences preferences = getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE);
            boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
            String userType = preferences.getString(KEY_USER_TYPE, "");

            Intent intent;
            if (isLoggedIn) {
                if ("Doctor".equals(userType)) {
                    intent = new Intent(Splash.this, DoctorDashboard.class);
                } else {
                    intent = new Intent(Splash.this, MainActivity.class);
                }
            } else {
                intent = new Intent(Splash.this, LoginActivity.class);
            }

            startActivity(intent);
            finish(); // Close Splash screen

        }, SPLASH_DELAY);
    }
}
