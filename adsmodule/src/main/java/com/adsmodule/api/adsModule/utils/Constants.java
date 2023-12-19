package com.adsmodule.api.adsModule.utils;

import com.adsmodule.api.adsModule.models.AdsResponseModel;

import java.util.ArrayList;

public class Constants {
    public static final String IS_ADS_FIRST_RUN = "IS_ADS_FIRST_RUN";
    public static final String STORED_BASE_URL = "STORED_BASE_URL";
    public static final String IS_APP_FIRST_RUN = "IS_APP_FIRST_RUN";
    public static final String PLAYSTORE_BASE = "https://play.google.com/store/apps/details?id=";
    public static final String IDE_BASE_URL = "https://www.api.ideadsdevspanel.com/api/";
    public static final String D2M_BASE_URL = "https://www.api.d2madsdevspanel.com/api/";
    public static String BASE_URL= D2M_BASE_URL;
    public static boolean IS_MOBILE_ADS_INITIALIZED= false;
    public static boolean IS_NETWORK_AVAILABLE= false;
    public static String IS_APP_OPEN_ADS = "app open ads";
    public static String BACKPRESS_AD_TYPE = "app open ads";
    public static boolean IS_TEST_AD = false;
    public static String TEST_ADS = "Test Ads";

    public static AdsResponseModel adsResponseModel= new AdsResponseModel();
    public static int hitCounter = 0;
    public static int BACKPRESS_COUNT = 0;
    public static ArrayList<String> platformList= new ArrayList<>();
    public static boolean isFixed = false;
    public static final String FIXED = "Fixed Ads";

    public static ArrayList<String> fixedPlatformList= new ArrayList<>();
    public static ArrayList<String> nativePlatformList= new ArrayList<>();
    public static ArrayList<String> interstitialPlatformList= new ArrayList<>();
    public static ArrayList<String> appOpenPlatformList= new ArrayList<>();
    public static ArrayList<String> rewardedPlatformList= new ArrayList<>();
    public static ArrayList<String> bannerPlatformList= new ArrayList<>();
    public static ArrayList<String> backPressAdPlatformList= new ArrayList<>();
    public static ArrayList<String> collapsibleAdPlatformList= new ArrayList<>();
}
