package com.adsmodule.api.adsModule.retrofit;

import static java.util.Objects.isNull;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adsmodule.api.adsModule.enums.Panel;
import com.adsmodule.api.adsModule.models.AdsDataRequestModel;
import com.adsmodule.api.adsModule.models.AdsResponseModel;
import com.adsmodule.api.adsModule.utils.AdUtils;
import com.adsmodule.api.adsModule.utils.AppInterfaces;
import com.adsmodule.api.adsModule.utils.Constants;
import com.adsmodule.api.adsModule.utils.Globals;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsApiHandler {

    private static final String TAG = "AdsApiHandler";

    public static void callAppCountApi(Panel panel, AdsDataRequestModel adsDataRequestModel, AppInterfaces.DownloadResponseInterface responseInterface) {
        Constants.BASE_URL = panel == Panel.IDE ? Constants.IDE_BASE_URL : Constants.D2M_BASE_URL;
        AdsApiInterface adsApiInterface = AdsApiClient.getInstance().getClient(Constants.BASE_URL).create(AdsApiInterface.class);
        Call<String> call = adsApiInterface.registerAppCount(adsDataRequestModel);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    responseInterface.onCountChange(Constants.BASE_URL);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    public static void callAdsApi(Activity activity, String baseURL, AdsDataRequestModel requestModel, AppInterfaces.AdDataInterface adDataInterface) {
        AdsApiInterface apiInterface = AdsApiClient.getInstance().getClient(baseURL).create(AdsApiInterface.class);
        Call<AdsResponseModel> call = apiInterface.getAdsData(requestModel);
        long start = System.currentTimeMillis();
        call.enqueue(new Callback<AdsResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<AdsResponseModel> call, @NonNull Response<AdsResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long end = System.currentTimeMillis();
                    Log.e(TAG, "onResponse: API Call Response time - " + (end - start) + " milliseconds");
                    Constants.adsResponseModel = response.body();
                    if (Constants.platformList.size() > 0) Constants.platformList.clear();
                    Constants.hitCounter = Constants.adsResponseModel.getAds_count();
                    Constants.BACKPRESS_COUNT = Constants.adsResponseModel.getBackPress_count();
                    Constants.IS_TEST_AD = Constants.adsResponseModel.getAds_type().trim().equals(Constants.TEST_ADS);
                    if (!isNull(Constants.adsResponseModel.getMonetize_platform())) {
                        Constants.platformList.addAll(Arrays.asList(Constants.adsResponseModel.getMonetize_platform().split(",")));
                        Constants.isFixed = Constants.FIXED.compareToIgnoreCase(Constants.adsResponseModel.getAds_sequence_type()) == 0;
                        /*if(Constants.IS_TEST_AD){
                            if(Constants.platformList.size() > 0) Constants.platformList.clear();
                            Constants.platformList.add(AdPlatform.Adx.toString());
                            Constants.platformList.add(AdPlatform.Facebook.toString());
                            Constants.platformList.add(AdPlatform.Admob.toString());
                        }
                        else {

                        }*/
                        if (Constants.platformList.isEmpty()) Constants.platformList.add("Adx");
                        if (Constants.isFixed) {
                            Constants.fixedPlatformList.addAll(Constants.platformList);
                            Constants.nativePlatformList.addAll(Constants.platformList);
                            Constants.interstitialPlatformList.addAll(Constants.platformList);
                            Constants.appOpenPlatformList.addAll(Constants.platformList);
                            Constants.backPressAdPlatformList.addAll(Constants.platformList);
                            Constants.collapsibleAdPlatformList.addAll(Constants.platformList);
                        }
                    }

                    Log.e(TAG, "Platform List" + Constants.platformList);
                    if (Globals.checkAppVersion(Constants.adsResponseModel.getVersion_name(), activity)) {
                        Globals.showUpdateAppDialog(activity);
                    } else {
                        if (Constants.adsResponseModel.isShow_ads()) {
                            if (Constants.isFixed) {
                                // AdUtils.buildAppOpenCache(activity, Constants.adsResponseModel.getApp_open_ads().getAdx());
                                // FixedAdUtils.buildFixedNativeCache(activity);
                                // FixedAdUtils.buildFixedInterstitialCache(activity);
                                // AdUtils.buildRewardedCache(activity, Constants.adsResponseModel.getRewarded_ads().getAdx());
                            } else {
                                // AdUtils.buildAppOpenCache(activity, Constants.adsResponseModel.getApp_open_ads().getAdx());
                                AdUtils.buildNativeCache(activity);
                                AdUtils.buildInterstitialCache(activity, Constants.adsResponseModel.getInterstitial_ads().getAdx());
                                // AdUtils.buildRewardedCache(activity, Constants.adsResponseModel.getRewarded_ads().getAdx());
                            }
                        }
                        adDataInterface.getAdData(response.body());
                    }

                } else {
                    Log.e(TAG, "onFailure: API Call not success due to : " + response.message());
                    adDataInterface.getAdData(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdsResponseModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: API Call Failure due to : " + t.getMessage());
                adDataInterface.getAdData(null);
            }
        });
    }
}
