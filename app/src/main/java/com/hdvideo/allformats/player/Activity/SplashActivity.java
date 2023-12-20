package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.SingletonClasses.LifeCycleOwner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.adsmodule.api.adsModule.models.AdsDataRequestModel;
import com.adsmodule.api.adsModule.retrofit.AdsApiHandler;
import com.adsmodule.api.adsModule.utils.AdUtils;
import com.adsmodule.api.adsModule.utils.AppInterfaces;
import com.adsmodule.api.adsModule.utils.Globals;
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

        if(Globals.isConnectingToInternet(SplashActivity.this)){
            AdsApiHandler.callAdsApi(activity, com.adsmodule.api.adsModule.utils.Constants.BASE_URL, new AdsDataRequestModel(getPackageName(), ""), adsResponseModel -> {
                if(adsResponseModel!=null){
                    AdUtils.showAppStartAd(activity, adsResponseModel, new AppInterfaces.AppStartInterface() {
                        @Override
                        public void loadStatus(boolean isLoaded) {
                            nextActivity();
                        }
                    });
                }
                else new Handler().postDelayed(this::nextActivity, 1000);
            });
        }
        else new Handler().postDelayed(this::nextActivity, 1500);
    }
    private void nextActivity() {
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {

            boolean isFirstRun = preferences.getBoolean(Constants.isFirstRun, true);
            Log.e(TAG, "isFirstRun: " + isFirstRun);
            if (isFirstRun) {
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class).putExtra("activityName", "sp"));
                finish();
//                preferences.putString(SELECTED_LANGUAGE, "en");
            } else {
//                String lang = preferences.getString(SELECTED_LANGUAGE);
//                LocaleHelper.setLocale(SplashActivity.this, lang, "db");
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class).putExtra("type", 111));
                finish();
            }

//        }, 1500);
    }

}