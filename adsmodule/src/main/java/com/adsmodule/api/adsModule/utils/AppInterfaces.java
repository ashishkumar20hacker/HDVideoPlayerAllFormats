package com.adsmodule.api.adsModule.utils;

import com.adsmodule.api.adsModule.models.AdsResponseModel;

public class AppInterfaces {
    public interface DownloadResponseInterface{
        void onCountChange(String baseUrl);
    }

    public interface AdDataInterface {
        void getAdData(AdsResponseModel adsResponseModel);
    }

    public interface AppStartInterface{
        void loadStatus(boolean isLoaded);
    }

    public interface InterstitialAdCallback{
        void loadStatus(boolean isLoaded);
    }

    public interface FacebookInterface{
        void facebookStatus(boolean isLoaded);
    }

    public interface AppOpenAdCallback{
      void loadStatus(boolean isLoaded);
    }

    public interface RewardedAdCallback{
        void loadStatus(boolean isLoaded);
    }

    public interface BackPressAdCallback{
        void loadStatus(boolean isLoaded);
    }
}