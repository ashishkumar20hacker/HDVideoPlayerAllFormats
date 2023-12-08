package com.hdvideo.allformats.player.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.hdvideo.allformats.player.Extras.Constants;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    SharePreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.makeStatusBarTransparent2(this);
        setContentView(R.layout.activity_splash);

        preferences = new SharePreferences(this);

        nextActivity();

    }
    private void nextActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            boolean isFirstRun = preferences.getBoolean(Constants.isFirstRun, true);
            Log.e(TAG, "isFirstRun: " + isFirstRun);
            if (isFirstRun) {
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class).putExtra("activityName", "sp"));
                finish();
//                preferences.putString(SELECTED_LANGUAGE, "en");
            } else {
//                String lang = preferences.getString(SELECTED_LANGUAGE);
//                LocaleHelper.setLocale(SplashActivity.this, lang, "db");
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                finish();
            }

        }, 1500);
    }

}