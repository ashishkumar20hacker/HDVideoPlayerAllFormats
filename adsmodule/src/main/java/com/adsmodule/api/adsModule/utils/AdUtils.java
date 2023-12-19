package com.adsmodule.api.adsModule.utils;

import static com.adsmodule.api.adsModule.utils.Globals.isNull;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.adsmodule.api.adsModule.R;
import com.adsmodule.api.adsModule.enums.AdPlatform;
import com.adsmodule.api.adsModule.enums.NativeAdType;
import com.adsmodule.api.adsModule.models.AdsResponseModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AdUtils {

    private static final String TAG = "AdUtils";
    public static NativeAd preCacheNativeAd;
    public static InterstitialAd preCacheInterstitialAd;
    public static AppOpenAd preCacheAppOpenAd;
    public static RewardedAd preCacheRewardedAd;

    public static int adsHitCount;
    public static int adsBackPressHitCount;
    private static Handler handler = new Handler();
    private static int nativeAdCounter = 0;
    private static int interstitialAdCounter = 0;
    private static int backPressAdCounter = 0;
    private static int rewardAdCounter = 0;
    private static int bannerAdCounter = 0;
    private static int appOpenAdCounter = 0;
    private static int collapsibleAdCounter = 0;

    //TODO: Show App Start Ads
    public static void showAppStartAd(Activity activity, AdsResponseModel adsResponseModel, AppInterfaces.AppStartInterface appStartInterface){
        if(adsResponseModel.getAds_open_type().equals(Constants.IS_APP_OPEN_ADS)){
            AdUtils.showAppOpenAd(activity, appStartInterface::loadStatus);
        }
        else {
            AdUtils.showInterstitialAd(activity, appStartInterface::loadStatus);
        }
    }


    //TODO: Native Ads Implementation
    public static void showNativeAd(Activity activity, CardView cardView, NativeAdType adType, Drawable adPlaceHolder) {
/*        if(adPlaceHolder != null){
            cardView.removeAllViews();
            cardView.addView(Globals.getAdPlaceholderImage(activity, adPlaceHolder, adType));
            cardView.setVisibility(View.VISIBLE);
        }*/
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            Log.e(TAG, "Native Ad: Is Pre cache null ? " + (preCacheNativeAd != null));



            if (!Constants.isFixed) {
                Collections.shuffle(Constants.platformList);
                if (Constants.platformList.get(0).equals(AdPlatform.Facebook.toString())) {
                    Log.e(TAG, "Native Ad: Facebook Platform Ad triggered");
                    String nativeAdUnitId = getNativeAdUnitId();
                    showFacebookNativeAd(activity, nativeAdUnitId, adType, cardView, new AppInterfaces.FacebookInterface() {
                        @Override
                        public void facebookStatus(boolean isLoaded) {
                            if (!isLoaded) {
                                nativeAdCounter++;
                                if (nativeAdCounter < Constants.platformList.size()) {
                                    showNativeAd(activity, cardView, adType, adPlaceHolder);
                                } else {
                                    nativeAdCounter = 0;
                                }
                            }else{
                                nativeAdCounter = 0;
                            }
                        }
                    });
                } else {
                    if (preCacheNativeAd == null) {

                        String nativeAdUnitId = getNativeAdUnitId();
                        // "ca-app-pub-3940256099942544/2247696110" -> Test ID
                        AdLoader adLoader = new AdLoader.Builder(activity, nativeAdUnitId)
                                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                                    @Override
                                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                                        Log.e(TAG, "NativeAds: Ads Loaded");
                                        if (!Constants.isFixed || nativeAdCounter < Constants.platformList.size())
                                            nativeAdCounter = 0;
                                        loadNativeAd(nativeAd, activity, adType, cardView);
                                    }
                                })
                                .withAdListener(new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                        Log.e(TAG, "NativeAds: Ads Load Failed");
                                        nativeAdCounter++;
                                        if (nativeAdCounter < Constants.platformList.size()) {
                                            showNativeAd(activity, cardView, adType, adPlaceHolder);
                                        } else {
                                            nativeAdCounter = 0;
                                        }
                                    }
                                })
                                .withNativeAdOptions(new NativeAdOptions.Builder().build()).build();
                        adLoader.loadAd(new AdRequest.Builder().build());

                    } else loadNativeAd(preCacheNativeAd, activity, adType, cardView);
                }
            }
            else FixedAdUtils.showFixedNativeAd(activity, cardView, adType);
        }else{
            if (adPlaceHolder != null) {
                cardView.removeAllViews();
                cardView.addView(Globals.getAdPlaceholderImage(activity, adPlaceHolder, adType));
            } /*else cardView.setVisibility(View.GONE);*/
        }
    }

    private static void showFacebookNativeAd(Activity activity, String nativeAdUnitId, NativeAdType adType, CardView cardView, AppInterfaces.FacebookInterface facebookInterface) {

        com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(activity, nativeAdUnitId);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                facebookInterface.facebookStatus(true);
                inflateFaceBookAd(activity, adType, (com.facebook.ads.NativeAd) ad, cardView);
            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                facebookInterface.facebookStatus(false);
                Log.e(TAG, "inflateFaceBookAd Execption: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private static void inflateFaceBookAd(Activity activity, NativeAdType adType, com.facebook.ads.NativeAd facebookAd, CardView adContainer) {
        try {
            adContainer.removeAllViews();
//        adContainer.setCardBackgroundColor(Color.parseColor(Constants.adsResponseModel.getAd_bg()));
            adContainer.setVisibility(View.VISIBLE);
            facebookAd.unregisterView();

            LayoutInflater inflater = LayoutInflater.from(activity);
            View view;
            if (adType.equals(NativeAdType.FULL) || adType.equals(NativeAdType.MEDIUM)) {
                view = inflater.inflate(R.layout.ads_nb_fb_full, null);
            } else {
                view = inflater.inflate(R.layout.ads_nb_fb, null);
            }

            adContainer.addView(view);
            NativeAdLayout nativeAdLayout = view.findViewById(R.id.nativview);
            LinearLayout adChoicesContainer = view.findViewById(R.id.ad_choices_container);
            AdOptionsView adOptionsView = new AdOptionsView(activity, facebookAd, nativeAdLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);
            TextView nativeAdCallToAction = view.findViewById(R.id.nb_ad_call_to_action);

            TextView nativeAdTitle = view.findViewById(R.id.native_ad_title);
//        nativeAdTitle.setTextColor(Color.parseColor(Constants.adsResponseModel.getCommon_text_color()));

            TextView nativeAdSocialContext = view.findViewById(R.id.native_ad_social_context);
//        nativeAdSocialContext.setTextColor(Color.parseColor(Constants.adsResponseModel.getCommon_text_color()));
            com.facebook.ads.MediaView nativeAdIconView = view.findViewById(R.id.native_icon_view);

            nativeAdCallToAction.setText(facebookAd.getAdCallToAction());

//        nativeAdCallToAction.setTextColor(Color.parseColor(Constants.adsResponseModel.getButton_text_color()));
            nativeAdCallToAction.setVisibility(facebookAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);

            nativeAdTitle.setText(facebookAd.getAdvertiserName());
//        nativeAdTitle.setTextColor(Color.parseColor(Constants.adsResponseModel.getCommon_text_color()));

            nativeAdSocialContext.setText(facebookAd.getAdBodyText());
//        nativeAdSocialContext.setTextColor(Color.parseColor(Constants.adsResponseModel.getCommon_text_color()));

            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);
            clickableViews.add(nativeAdIconView);
            clickableViews.add(nativeAdSocialContext);
            facebookAd.registerViewForInteraction(view, nativeAdIconView, clickableViews);
        } catch (Exception e) {
            Log.e(TAG, "inflateFaceBookAd Execption: " + e.getLocalizedMessage());
        }

    }

    private static String getNativeAdUnitId() {
        String adTag = Constants.platformList.get(nativeAdCounter);
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getNative_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getNative_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getNative_ads().getAdmob() : "";
    }

    private static void loadNativeAd(@NonNull NativeAd nativeAd, Activity activity, NativeAdType adType, CardView cardView) {

        NativeAdView unifiedNativeAdView;
        if (adType.equals(NativeAdType.MEDIUM)) {
            unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.medium_native_ad, (ViewGroup) null);
        } else if (adType.equals(NativeAdType.FULL)) {
            unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.full_native_ad, (ViewGroup) null);
        } else {
            unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.small_native_ad, (ViewGroup) null);
        }
        populateNativeAd(nativeAd, unifiedNativeAdView, adType);
        cardView.removeAllViews();
        cardView.setCardBackgroundColor(Color.parseColor(!isNull(Constants.adsResponseModel.getAd_bg()) ? Constants.adsResponseModel.getAd_bg() : "#FFFFFF"));
        cardView.addView(unifiedNativeAdView);
        cardView.setVisibility(View.VISIBLE);
        buildNativeCache(activity); // Build the precache for the next native Ad
    }

    public static void buildNativeCache(Activity activity) {
        preCacheNativeAd = null;
        Log.e(TAG, "NativeAds: Building native precache");
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {

            String nativeAdUnitId = getNativeAdUnitId();
            if (Constants.platformList.get(nativeAdCounter).equals(AdPlatform.Facebook.toString())) {
                Log.e(TAG, "Native Ad: Facebook Platform Ad triggered");
            } else {
                // "ca-app-pub-3940256099942544/2247696110" -> Test ID
                AdLoader adLoader = new AdLoader.Builder(activity, nativeAdUnitId)
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                                if (!Constants.isFixed || nativeAdCounter < Constants.platformList.size())
                                    nativeAdCounter = 0;
                                preCacheNativeAd = nativeAd; // Storing the nativeAd
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                Log.e(TAG, "NativeAds: Fail to Load");
                                nativeAdCounter++;
                                if (nativeAdCounter < Constants.platformList.size()){
                                    buildNativeCache(activity);
                                }
                                else nativeAdCounter = 0;
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder().build()).build();
                adLoader.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    public static void populateNativeAd(NativeAd nativeAd, NativeAdView nativeAdView, NativeAdType adType) {
        // Populating/Displaying the Native Ads into the Container/CardView (from the XML Layout)
        if (adType.equals(NativeAdType.FULL)) {
            nativeAdView.setMediaView(nativeAdView.findViewById(R.id.ad_media));
        }
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) Objects.requireNonNull(nativeAdView.getHeadlineView())).setText(nativeAd.getHeadline());
        ((TextView) nativeAdView.getHeadlineView()).setTextColor(!isNull(Constants.adsResponseModel.getCommon_text_color()) ? Color.parseColor(Constants.adsResponseModel.getCommon_text_color()) : Color.BLACK);
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(nativeAdView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            //TODO sets color to headline button -->
            Objects.requireNonNull(nativeAdView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
            ((TextView) nativeAdView.getBodyView()).setTextColor(!isNull(Constants.adsResponseModel.getCommon_text_color()) ? Color.parseColor(Constants.adsResponseModel.getCommon_text_color()) : Color.BLACK);
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(nativeAdView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(nativeAdView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
            //TODO sets color to install button -->
            nativeAdView.getCallToActionView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Constants.adsResponseModel.getButton_bg())));
            ((Button) nativeAdView.getCallToActionView()).setTextColor(!isNull(Constants.adsResponseModel.getButton_text_color()) ? Color.parseColor(Constants.adsResponseModel.getButton_text_color()) : Color.WHITE);
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(nativeAdView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(nativeAdView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(nativeAdView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(nativeAdView.getPriceView()).setVisibility(View.GONE);
            ((TextView) nativeAdView.getPriceView()).setText(nativeAd.getPrice());
            ((TextView) nativeAdView.getPriceView()).setTextColor(!isNull(Constants.adsResponseModel.getCommon_text_color()) ? Color.parseColor(Constants.adsResponseModel.getCommon_text_color()) : Color.BLACK);

        }
        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(nativeAdView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(nativeAdView.getStoreView()).setVisibility(View.GONE);
            ((TextView) nativeAdView.getStoreView()).setText(nativeAd.getStore());
            ((TextView) nativeAdView.getStoreView()).setTextColor(!isNull(Constants.adsResponseModel.getCommon_text_color()) ? Color.parseColor(Constants.adsResponseModel.getCommon_text_color()) : Color.BLACK);
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(nativeAdView.getStarRatingView()).setVisibility(View.VISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(nativeAdView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            nativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
            ((RatingBar) nativeAdView.getStarRatingView()).setProgressBackgroundTintList(!isNull(Constants.adsResponseModel.getButton_bg()) ? ColorStateList.valueOf(Color.parseColor(Constants.adsResponseModel.getButton_bg())) : ColorStateList.valueOf(Color.BLACK));
            ((RatingBar) nativeAdView.getStarRatingView()).setProgressTintList(!isNull(Constants.adsResponseModel.getButton_bg()) ? ColorStateList.valueOf(Color.parseColor(Constants.adsResponseModel.getButton_bg())) : ColorStateList.valueOf(Color.BLACK));
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(nativeAdView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(nativeAdView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            nativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getAdvertiserView()).setTextColor(!isNull(Constants.adsResponseModel.getCommon_text_color()) ? Color.parseColor(Constants.adsResponseModel.getCommon_text_color()) : Color.BLACK);
        }
        if (adType.equals(NativeAdType.MEDIUM)) {
            nativeAdView.getBodyView().setVisibility(View.VISIBLE);
            nativeAdView.getIconView().setVisibility(View.GONE);
            nativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        nativeAdView.setNativeAd(nativeAd);

    }


    //TODO: Interstitial Ads Implementation
    public static void showInterstitialAd(Activity activity, AppInterfaces.InterstitialAdCallback interstitialAdCallback) {
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            if (Constants.hitCounter == adsHitCount) {
                if(Globals.loading_dialog!=null && !Globals.loading_dialog.isShowing())
                    Globals.showLoadingDialog(activity);
                if(!Constants.isFixed){
                    Collections.shuffle(Constants.platformList);
                    if (Constants.platformList.get(0).equals(AdPlatform.Facebook.toString())) {
                        Log.e(TAG, "Native Ad: Facebook Platform Ad triggered");
                        showFacebookInterstitialAd(activity, getInterstitialAdUnitId(Constants.platformList.get(0)), new AppInterfaces.FacebookInterface() {
                            @Override
                            public void facebookStatus(boolean isLoaded) {
                                if (!isLoaded) {
                                    interstitialAdCounter++;
                                    if (interstitialAdCounter < Constants.platformList.size()) {
                                        showInterstitialAd(activity, interstitialAdCallback);
                                    } else {
                                        interstitialAdCounter = 0;
                                        interstitialAdCallback.loadStatus(false);
                                        Globals.hideLoadingDialog();
                                    }
                                }else{
                                    adsHitCount = 0;
                                    interstitialAdCounter = 0;
                                    interstitialAdCallback.loadStatus(true);
                                    Globals.hideLoadingDialog();
                                }
                            }
                        });
                    }
                    else {
                        if (preCacheInterstitialAd == null) {
                            AdRequest adRequest = new AdRequest.Builder().build();
                            String interstitialAdUnitId = getInterstitialAdUnitId(Constants.platformList.get(interstitialAdCounter));

                            InterstitialAd.load(activity, interstitialAdUnitId, adRequest,
                                    new InterstitialAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                            // Interstitial ad is loaded.
                                            loadInterstitialAd(interstitialAd, interstitialAdCallback, activity);
                                            interstitialAdCounter = 0;
                                            Log.i(TAG, "Interstitial Ad: onAdLoaded First Time");
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            // Handle the error
                                            Log.d(TAG, "Interstitial Ad: Ads Failed to Load : " + loadAdError);

                                            if (interstitialAdCounter < Constants.platformList.size()) {
                                                interstitialAdCounter++;
                                                showInterstitialAd(activity, interstitialAdCallback);
                                            } else {
                                                interstitialAdCounter = 0;
                                                Globals.hideLoadingDialog();
                                                interstitialAdCallback.loadStatus(false);
                                            }
                                        }
                                    });
                        }
                        else {
                            //Loading From PreCache
                            loadInterstitialAd(preCacheInterstitialAd, interstitialAdCallback, activity);
                            Log.i(TAG, "Interstitial Ad: Loaded From Precache");
                        }
                    }
                }
                else FixedAdUtils.showFixedInterstitialAd(activity, interstitialAdCallback);
            }
            else {
                adsHitCount++;
                interstitialAdCallback.loadStatus(false);
                Globals.hideLoadingDialog();
            }
        }
        else {
            interstitialAdCallback.loadStatus(false);
            Globals.hideLoadingDialog();
        }
    }

    private static void loadInterstitialAd(@NonNull InterstitialAd interstitialAd, AppInterfaces.InterstitialAdCallback interstitialAdCallback, Activity activity) {
        //Interstitial Ad CallBack
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Interstitial Ad: Ad dismissed fullscreen content.");
                handler.post(() -> interstitialAdCallback.loadStatus(true));
                // interstitialAdCallback.loadStatus(true);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Interstitial Ad: Ad failed to show fullscreen content." + adError.getMessage());
                interstitialAdCallback.loadStatus(false);
                Globals.hideLoadingDialog();

            }

            @Override
            public void onAdShowedFullScreenContent() {
                adsHitCount = 0;
                Globals.hideLoadingDialog();
            }
        });
        buildInterstitialCache(activity, Constants.adsResponseModel.getInterstitial_ads().getAdx());
        // Show Interstitial Ad
        interstitialAd.show(activity);
        Globals.hideLoadingDialog();

    }

    public static void buildInterstitialCache(Activity activity, String interstitialAdUnitId) {
        preCacheInterstitialAd = null;
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            AdRequest adRequest = new AdRequest.Builder().build();

            // "ca-app-pub-3940256099942544/1033173712" -> Test ID
            InterstitialAd.load(activity, interstitialAdUnitId, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            preCacheInterstitialAd = interstitialAd;
                            Log.i(TAG, "Interstitial Ad: InterstitialAd PreCached");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d(TAG, "Interstitial Ad: " + loadAdError.toString());
                        }
                    });
        }
    }

    private static String getInterstitialAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getInterstitial_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getInterstitial_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getInterstitial_ads().getAdmob() : "";
    }

    public static void showFacebookInterstitialAd(Activity activity, String interstitialAdId, AppInterfaces.FacebookInterface facebookInterface){
        com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(activity, interstitialAdId);
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.e(TAG, "Facebook Interstitial Ad: Displayed ");

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.e(TAG, "Facebook Interstitial Ad: Dismissed ");

            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                Log.e(TAG, "Facebook Interstitial Ad: Error Occurred "+ adError.getErrorMessage() );
                facebookInterface.facebookStatus(false);

            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e(TAG, "Facebook ad loaded");
                facebookInterface.facebookStatus(true);
//                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }


    //TODO: AppOpen Ads Implementation
    public static void showAppOpenAd(Activity activity, AppInterfaces.AppOpenAdCallback appOpenAdCallback) {
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            Globals.showLoadingDialog(activity);
            if (!Constants.isFixed) {
                if (preCacheAppOpenAd == null) {

                    Collections.shuffle(Constants.platformList);
                    String adTag = Constants.platformList.get(0);
                    adTag= adTag.equals(AdPlatform.Facebook.toString()) ? AdPlatform.Adx.toString() : adTag;
                    String appOpenAdUnitId = getAppOpenAdUnitId(adTag);

                    AdRequest adRequest = new AdRequest.Builder().build();
                    AppOpenAd.load(activity, appOpenAdUnitId, adRequest, new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            // Handle Failed To load
                            Globals.hideLoadingDialog();
                            appOpenAdCallback.loadStatus(false);
                            Log.e(TAG, "AppOpen Ad:  onAdFailedToLoad: " + loadAdError.getMessage());
                        }

                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                            super.onAdLoaded(appOpenAd);
                            // Loading App Open from new request
                            loadAppOpenAd(activity, appOpenAd, appOpenAdCallback);
                        }
                    });
                } else {
                    //Loading From PreCache
                    loadAppOpenAd(activity, preCacheAppOpenAd, appOpenAdCallback);
                    Log.i(TAG, "AppOpen Ad: Loaded From Precache");
                }
            }
            else FixedAdUtils.showFixedAppOpenAd(activity, appOpenAdCallback);
        }
        else {
            appOpenAdCallback.loadStatus(false);
        }
    }

    private static void loadAppOpenAd(Activity activity, AppOpenAd appOpenAd, AppInterfaces.AppOpenAdCallback appOpenAdCallback) {
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                // AppOpen Dismissed
                Log.e(TAG, "AppOpen Ad: Dismissed");
                handler.postDelayed(() -> appOpenAdCallback.loadStatus(true), 25);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                // Handle Failed To Load
                appOpenAdCallback.loadStatus(false);
                Log.e(TAG, "AppOpen Ad:  onAdFailedToLoad: " + adError.getMessage());
            }
        };

        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
        // Building AppOpen AD Cache
        // buildAppOpenCache(activity, Constants.adsResponseModel.getApp_open_ads().getAdx());
        // Show AppOpen Ad
        appOpenAd.show(activity);
        Globals.hideLoadingDialog();
    }

    public static void buildAppOpenCache(Activity activity, String appOpenAdUnitId) {
        preCacheAppOpenAd = null;
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(activity, appOpenAdUnitId, adRequest, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e(TAG, "AppOpen Ad:  onAdFailedToLoad: " + loadAdError.getMessage());
                }

                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    preCacheAppOpenAd = appOpenAd;
                    Log.e(TAG, "AppOpen Ad: AppOpen Ad PreCached");
                }
            });

        }

    }

    private static String getAppOpenAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getApp_open_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getApp_open_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getApp_open_ads().getAdmob() : "";
    }



    //TODO: Rewarded Ads Implementation
    public static void showRewardedAd(Activity activity, AppInterfaces.RewardedAdCallback rewardedAdCallback) {
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            Globals.showLoadingDialog(activity);
            if (!Constants.isFixed) {
                Collections.shuffle(Constants.platformList);
                if (preCacheRewardedAd == null) {
                    String rewardedAdUnitId = getRewardAdUnitId();
                    AdRequest adRequest = new AdRequest.Builder().build();
                    RewardedAd.load(activity, rewardedAdUnitId, adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Globals.hideLoadingDialog();
                            rewardedAdCallback.loadStatus(false);
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            Log.e(TAG, "Rewarded Ad: onAdLoaded First Time");
                            loadRewardedAd(activity, rewardedAd, rewardedAdCallback);
                        }

                    });
                } else {
                    //Loading From PreCache
                    Log.i(TAG, "Rewarded Ad: Loaded From Precache");
                    loadRewardedAd(activity, preCacheRewardedAd, rewardedAdCallback);
                }
            } else FixedAdUtils.showFixedRewardedAd(activity, rewardedAdCallback);
        } else {
            Globals.hideLoadingDialog();

            rewardedAdCallback.loadStatus(false);
        }
    }

    private static void loadRewardedAd(Activity activity, @NonNull RewardedAd rewardedAd, AppInterfaces.RewardedAdCallback rewardedAdCallback) {
        // Setting the fullscreen check callback for getting the successful response of the video
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                rewardedAdCallback.loadStatus(false);
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                handler.postDelayed(() -> rewardedAdCallback.loadStatus(true), 10); // This handler is the remove/avoid the black screen that comes after the ad
            }
        });
        // buildRewardedCache(activity, Constants.adsResponseModel.getRewarded_ads().getAdx()); // Loads & stores the next rewarded ad
        rewardedAd.show(activity, rewardItem -> {
            // rewardedAdCallback.loadStatus(true); // This callback automatically triggers when the reward video is finished. Can adjust this according to your requirement
        });
        Globals.hideLoadingDialog();
    }

    public static void buildRewardedCache(Activity activity, String rewardedAdUnitId) {
        preCacheRewardedAd = null;
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(activity, rewardedAdUnitId, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e(TAG, "Rewarded Ad: Rewarded Ad Failure due to : " + loadAdError.getMessage());
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    // The preCacheRewardedAd value will be changing from NULL to the response value
                    preCacheRewardedAd = rewardedAd;
                }
            });
        }
    }

    private static String getRewardAdUnitId() {
        String adTag = Constants.platformList.get(0);
        adTag = adTag.equals(AdPlatform.Facebook.toString()) ? AdPlatform.Adx.toString() : adTag;
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getRewarded_ads().getAdx()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getRewarded_ads().getAdmob() : "";
    }


    //TODO: BackPress Ads Implementation
    public static void showBackPressAd(Activity activity, AppInterfaces.BackPressAdCallback backPressAdCallback) {
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            if (Constants.BACKPRESS_COUNT == adsBackPressHitCount) {
                Globals.showLoadingDialog(activity);
                String backPressType = Constants.adsResponseModel.getBackPress_ads_type();
                if (Globals.checkStringNull(backPressType)) {
                    if (backPressType.equals(Constants.BACKPRESS_AD_TYPE))
                        showBackPressAppOpenAd(activity, backPressAdCallback); // Calling AppOpenAd for the BackPress
                    else
                        showBackPressInterstitialAd(activity, backPressAdCallback); // Calling Interstitial Ad for the BackPress
                } else {
                    Globals.hideLoadingDialog();
                    backPressAdCallback.loadStatus(false);
                }
            } else {
                adsBackPressHitCount++;
                Globals.hideLoadingDialog();
                backPressAdCallback.loadStatus(false);
            }

        } else {
            Globals.hideLoadingDialog();
            backPressAdCallback.loadStatus(false);
        }
    }

    private static void showBackPressInterstitialAd(Activity activity, AppInterfaces.BackPressAdCallback backPressAdCallback) {
        if (preCacheInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            Collections.shuffle(Constants.platformList);
            if (Constants.platformList.get(0).equals(AdPlatform.Facebook.toString())) {
                Log.e(TAG, "backPress InterstitialAd: Facebook Platform Ad triggered");
                showFacebookInterstitialAd(activity, getInterstitialAdUnitId(Constants.platformList.get(0)), new AppInterfaces.FacebookInterface() {
                    @Override
                    public void facebookStatus(boolean isLoaded) {
                        if (!isLoaded) {
                            interstitialAdCounter++;
                            if (interstitialAdCounter < Constants.platformList.size()) {
                                showBackPressInterstitialAd(activity, backPressAdCallback);
                            } else {
                                interstitialAdCounter = 0;
                                backPressAdCallback.loadStatus(false);
                                Globals.hideLoadingDialog();
                            }
                        } else {
                            interstitialAdCounter = 0;
                            backPressAdCallback.loadStatus(true);
                            Globals.hideLoadingDialog();
                        }
                    }
                });
            } else {
                String backPressInterstitialAdUnitId = getInterstitialAdUnitId(Constants.platformList.get(interstitialAdCounter));
                InterstitialAd.load(activity, backPressInterstitialAdUnitId, adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // Interstitial ad is loaded.
                                loadBackPressInterstitialAd(activity, interstitialAd, backPressAdCallback);
                                interstitialAdCounter = 0;
                                Log.i(TAG, "Interstitial Ad: onAdLoaded First Time");
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                if (interstitialAdCounter < Constants.platformList.size()) {
                                    interstitialAdCounter++;
                                    showBackPressInterstitialAd(activity, backPressAdCallback);
                                } else {
                                    interstitialAdCounter = 0;
                                    Globals.hideLoadingDialog();
                                    backPressAdCallback.loadStatus(false);
                                }
                                Log.d(TAG, "Interstitial Ad: Ads Failed to Load : " + loadAdError);
                            }
                        });
            }
        } else {
            //Loading From PreCache
            loadBackPressInterstitialAd(activity, preCacheInterstitialAd, backPressAdCallback);
            Log.i(TAG, "Interstitial Ad: Loaded From Precache");
        }
    }

    private static void loadBackPressInterstitialAd(Activity activity, @NonNull InterstitialAd interstitialAd, AppInterfaces.BackPressAdCallback backPressAdCallback) {
        //Interstitial Ad CallBack
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Interstitial Ad: Ad dismissed fullscreen content.");
                handler.postDelayed(() -> backPressAdCallback.loadStatus(true), 0);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Interstitial Ad: Ad failed to show fullscreen content." + adError.getMessage());
                backPressAdCallback.loadStatus(false);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                adsBackPressHitCount = 0;
            }
        });
        buildInterstitialCache(activity, Constants.adsResponseModel.getInterstitial_ads().getAdx());
        // Show Interstitial Ad
        interstitialAd.show(activity);
        Globals.hideLoadingDialog();

    }

    private static void showBackPressAppOpenAd(Activity activity, AppInterfaces.BackPressAdCallback backPressAdCallback) {
        if (preCacheAppOpenAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            Collections.shuffle(Constants.platformList);
            String adTag = Constants.platformList.get(0);
            adTag = adTag.equals(AdPlatform.Facebook.toString()) ? AdPlatform.Adx.toString() : adTag;

            AppOpenAd.load(activity, getAppOpenAdUnitId(adTag), adRequest, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    // Handle Failed To load
                    Globals.hideLoadingDialog();
                    handler.postDelayed(() -> backPressAdCallback.loadStatus(false), 25);
                    Log.e(TAG, "AppOpen Ad:  onAdFailedToLoad: " + loadAdError.getMessage());
                }

                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    // Loading App Open from new request
                    loadBackPressAppOpenAd(activity, appOpenAd, backPressAdCallback);
                }
            });
        } else {
            //Loading From PreCache
            loadBackPressAppOpenAd(activity, preCacheAppOpenAd, backPressAdCallback);
            Log.i(TAG, "AppOpen Ad: Loaded From Precache");
        }
    }

    private static void loadBackPressAppOpenAd(Activity activity, @NonNull AppOpenAd appOpenAd, AppInterfaces.BackPressAdCallback backPressAdCallback) {
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                // AppOpen Dismissed
                Log.e(TAG, "AppOpen Ad: Dismissed");
                backPressAdCallback.loadStatus(true);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                // Handle Failed To Load
                backPressAdCallback.loadStatus(false);
                Log.e(TAG, "AppOpen Ad:  onAdFailedToLoad: " + adError.getMessage());
            }
        };

        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
        // Building AppOpen AD Cache
        buildAppOpenCache(activity, Constants.adsResponseModel.getApp_open_ads().getAdx());
        // Show AppOpen Ad
        appOpenAd.show(activity);
        Globals.hideLoadingDialog();
    }



    //TODO: Banner Ads Implementation
    public static void showBannerAd(Activity activity, MaterialCardView adContainer) {
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            if (!Constants.isFixed) {
                Collections.shuffle(Constants.platformList);
                String adUnitId = getBannerAdUnitId();
                Log.d(TAG, "Banner Ad: Unit ID - " + adUnitId);
                if (Constants.platformList.get(0).equals(AdPlatform.Facebook.toString())) {
                    com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, adUnitId, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                    adContainer.addView(adView);
                    com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            bannerAdCounter++;
                            if (bannerAdCounter < Constants.platformList.size()) {
                                showBannerAd(activity, adContainer);
                            } else {
                                bannerAdCounter = 0;
                            }
                            Log.e(TAG, "FaceBook Banner Ad: Error "+adError.getErrorMessage() );
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            bannerAdCounter = 0;
                            Log.e(TAG, "FaceBook Banner Ad: Ad Loaded" );
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };
                    adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
                }
                else{
                    AdView bannerView = new AdView(activity);
                    bannerView.setAdSize(AdSize.BANNER);
                    bannerView.setAdUnitId(adUnitId);
                    bannerView.setAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            // Handle Error
                            bannerAdCounter++;
                            if(bannerAdCounter < Constants.platformList.size()){
                                showBannerAd(activity, adContainer);
                            }
                            else{
                                bannerAdCounter = 0;
                            }
                            Log.d(TAG, "BannerAd: onAdFailedToLoad: " + loadAdError.getMessage());
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            bannerAdCounter = 0;
                            Log.d(TAG, "BannerAd: Ad Loaded");

                        }

                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                            Log.d(TAG, "BannerAd: Ad Opened");
                        }
                    });
                    AdRequest.Builder adRequestBuilder = new AdRequest.Builder(); // AdRequest Builder
                    adContainer.addView(bannerView); // AdView is being added to the MaterialCardView which acts as container
                    bannerView.loadAd(adRequestBuilder.build());
                }
            }
            else FixedAdUtils.showFixedBannerAd(activity, adContainer);
        }
        else {
            Log.d(TAG, "BannerAd: No Internet");
        }
    }

    private static String getBannerAdUnitId() {
        String adTag = Constants.platformList.get(0);
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getBanner_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getBanner_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getBanner_ads().getAdmob() : "";
    }



    //TODO: Collapsible Banner Ads Implementation
    public static void showCollapsibleBannerAd(Activity activity, MaterialCardView adContainer) {
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.isShow_ads()) {
            if (!Constants.isFixed) {
                Collections.shuffle(Constants.platformList);
                String adUnitId = getCollapsibleBannerAdUnitId();
                AdView bannerView = new AdView(activity);
                adContainer.addView(bannerView); // AdView is being added to the MaterialCardView which acts as container

                bannerView.setAdSize(getAdSize(activity, adContainer));
                bannerView.setAdUnitId(adUnitId);
                bannerView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        collapsibleAdCounter++;
                        if (collapsibleAdCounter < Constants.platformList.size()) {
                            showCollapsibleBannerAd(activity, adContainer);
                        } else collapsibleAdCounter = 0;
                        // Handle Error
                        Log.d(TAG, "CollapsibleBannerAd: onAdFailedToLoad: " + loadAdError.getMessage());
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d(TAG, "CollapsibleBannerAd: Ad Loaded");

                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d(TAG, "CollapsibleBannerAd: Ad Opened");
                    }
                });
                Bundle extras = new Bundle();
                extras.putString("collapsible", "bottom");
                AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build(); // AdRequest Builder
                bannerView.loadAd(adRequest);
            } else FixedAdUtils.showFixedCollapsibleBannerAd(activity, adContainer);
        } else {
            Log.d(TAG, "CollapsibleBannerAd: No Internet");
        }
    }

    private static String getCollapsibleBannerAdUnitId() {
        String adTag = Constants.platformList.get(0);
        if(adTag.equals("Facebook")){
            if(Constants.platformList.size() > 1){
                adTag = Constants.platformList.get(1);
            }else{
                adTag = "";
            }
        }
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getCollapsible_ads().getAdx()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getCollapsible_ads().getAdmob() : "";
    }

    public static AdSize getAdSize(Activity activity, MaterialCardView adContainerView) {
        float adContainerWidth = adContainerView.getWidth();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int adWidthPixels = adContainerWidth > 0 ? (int) adContainerWidth : displayMetrics.widthPixels;

        float density = activity.getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
