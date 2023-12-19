package com.adsmodule.api.adsModule.utils;

import android.content.Context;

import androidx.lifecycle.LiveData;
import com.google.android.gms.ads.MobileAds;

public class MobileAdInitializer extends LiveData<Boolean>{

    private static final String TAG = "MobileAdInitializer";
    public MobileAdInitializer(Context context){
        MobileAds.initialize(context, initializationStatus -> {
            Constants.IS_MOBILE_ADS_INITIALIZED= true;
            MobileAdInitializer.this.postValue(true);
        });
    }
}
