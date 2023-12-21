package com.hdvideo.allformats.player.SingletonClasses;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.adsmodule.api.adsModule.utils.AppInterfaces;
import com.adsmodule.api.adsModule.utils.Constants;
import com.hdvideo.allformats.player.Activity.MPlayerActivity;
import com.hdvideo.allformats.player.Activity.SplashActivity;
import com.hdvideo.allformats.player.Activity.VideoPlayerActivity;

public class LifeCycleOwner implements DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    private static final String TAG = "LifeCycleOwner";
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    boolean isAdShowing;


    public LifeCycleOwner(MyApplication application) {
        application.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        isAdShowing = false;
    }
    
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (!activity.getComponentName().getClassName().equals("com.google.android.gms.ads.AdActivity")) {
            LifeCycleOwner.activity = activity;
            // This sets the default shared preference value for Custom Native AdsView.
            // This constraints multiple custom native views inside a same activity should be rendered in different time
            MyApplication.preferences.putInt("NATIVE_DELAY", 100);
        }
        Log.d(TAG, "onActivityCreated: "+ Constants.adsResponseModel.getPackage_name());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!activity.getComponentName().getClassName().equals("com.google.android.gms.ads.AdActivity"))
            LifeCycleOwner.activity = activity;
        Log.d(TAG, "onActivityStarted: ");
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (Constants.adsResponseModel.getPackage_name() != null && Constants.adsResponseModel.isShow_ads()) {
            if (!isAdShowing && LifeCycleOwner.activity != null && (!LifeCycleOwner.activity.getClass().getName().equals(SplashActivity.class.getName())) && (!LifeCycleOwner.activity.getClass().getName().equals(MPlayerActivity.class.getName())) && (!LifeCycleOwner.activity.getClass().getName().equals(VideoPlayerActivity.class.getName()))) {
                isAdShowing = true;
                AdUtils.showAppOpenAd(activity, new AppInterfaces.AppOpenAdCallback() {
                    @Override
                    public void loadStatus(boolean isLoaded) {
                        isAdShowing = false;
                    }
                });
            }
            else{
                isAdShowing = false;
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (!activity.getComponentName().getClassName().equals("com.google.android.gms.ads.AdActivity"))
            LifeCycleOwner.activity = activity;
        Log.d(TAG, "onActivityResumed: ");
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d(TAG, "onActivityPaused: ");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d(TAG, "onActivityStopped: ");
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState: ");
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d(TAG, "onActivityDestroyed: ");
    }
}
