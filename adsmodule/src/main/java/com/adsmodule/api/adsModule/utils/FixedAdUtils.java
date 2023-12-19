package com.adsmodule.api.adsModule.utils;

import static com.adsmodule.api.adsModule.utils.Globals.isNull;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.List;
import java.util.Objects;

public class FixedAdUtils {

    private static final String TAG = "FixedAdUtils";

    private static Handler handler = new Handler();
    private static int fixedNativeAdCounter = 0;
    private static int fixedInterstitialAdCounter = 0;
    private static int fixedAppOpenAdCounter = 0;
    private static int fixedRewardedAdCounter = 0;
    private static int fixedBannerAdCounter = 0;
    private static int fixedCollapsibleAdCounter = 0;

    //TODO: Native Ads Implementation
    public static void showFixedNativeAd(Activity activity, CardView cardView, NativeAdType adType) {
        Constants.nativePlatformList.clear();
        Constants.nativePlatformList.addAll(Constants.platformList);
        if (fixedNativeAdCounter >= Constants.nativePlatformList.size()) {
            fixedNativeAdCounter = 0;
        }
        String tempAdPlatformType = Constants.nativePlatformList.get(fixedNativeAdCounter);
        if (tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
            Log.e(TAG, "Native Ad: Facebook Platform Ad triggered");
            showFixedFacebookNativeAd(activity, getFixedNativeAdUnitId(tempAdPlatformType), adType, cardView, new AppInterfaces.FacebookInterface() {
                @Override
                public void facebookStatus(boolean isLoaded) {
                    if (!isLoaded) {
                        Log.e(TAG, "Native Ad: Facebook Ad Failed");
                        fixedNativeAdCounter++;
                        if (fixedNativeAdCounter < Constants.nativePlatformList.size()) {
                            showFixedNativeAd(activity, cardView, adType);
                        } else {
                            fixedNativeAdCounter = 0;
                        }
                    } else {
                        fixedNativeAdCounter = 0;
                    }
                }
            });
        }
        else {
            if (AdUtils.preCacheNativeAd == null) {
                AdLoader adLoader = new AdLoader.Builder(activity, getFixedNativeAdUnitId(tempAdPlatformType)) // "ca-app-pub-3940256099942544/2247696110" -> Test ID
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                                Log.e(TAG, "NativeAds: Ads Loaded");
                                fixedNativeAdCounter++;
                                loadFixedNativeAd(nativeAd, activity, adType, cardView);
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                Log.e(TAG, "NativeAds: Ads Load Failed");
                                fixedNativeAdCounter++;
                                if (fixedNativeAdCounter < Constants.nativePlatformList.size()) {
                                    showFixedNativeAd(activity, cardView, adType);
                                }
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder().build()).build();
                adLoader.loadAd(new AdRequest.Builder().build());
            } else loadFixedNativeAd(AdUtils.preCacheNativeAd, activity, adType, cardView);
        }
    }

    private static String getFixedNativeAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getNative_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getNative_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getNative_ads().getAdmob() : "";
    }

    private static void loadFixedNativeAd(@NonNull NativeAd nativeAd, Activity activity, NativeAdType adType, CardView cardView) {
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
        buildFixedNativeCache(activity, adType, cardView); // Build the precache for the next native Ad
    }

    public static void buildFixedNativeCache(Activity activity, NativeAdType adType, CardView cardView) {
        AdUtils.preCacheNativeAd = null;
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.getPackage_name() != null) {
            if (fixedNativeAdCounter > Constants.nativePlatformList.size() - 1) {
                fixedNativeAdCounter = 0;
            }
            String tempAdPlatformType = Constants.nativePlatformList.get(fixedNativeAdCounter);
            if (tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
                Log.e(TAG, "Native Ad: Facebook Platform Ad triggered");
                showFixedFacebookNativeAd(activity, getFixedNativeAdUnitId(tempAdPlatformType), adType, cardView, new AppInterfaces.FacebookInterface() {
                    @Override
                    public void facebookStatus(boolean isLoaded) {
                        fixedNativeAdCounter++;
                        if (!isLoaded && fixedNativeAdCounter < Constants.nativePlatformList.size()) {
                            buildFixedNativeCache(activity, adType, cardView);
                        }
                    }
                });
            }
            else {
                Log.e(TAG, "Native Ad: The cache native ad type is : " + Constants.nativePlatformList.get(fixedNativeAdCounter));
                // "ca-app-pub-3940256099942544/2247696110" -> Test ID
                AdLoader adLoader = new AdLoader.Builder(activity, getFixedNativeAdUnitId(tempAdPlatformType))
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                                fixedNativeAdCounter++;
                                AdUtils.preCacheNativeAd = nativeAd; // Storing the nativeAd
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                Log.e(TAG, "NativeAds: Fail to Load");
                                fixedNativeAdCounter++;
                                if (fixedNativeAdCounter < Constants.nativePlatformList.size()) {
                                    buildFixedNativeCache(activity, adType, cardView);
                                }
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

    private static void showFixedFacebookNativeAd(Activity activity, String nativeAdUnitId, NativeAdType adType, CardView cardView, AppInterfaces.FacebookInterface facebookInterface) {

        com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(activity, nativeAdUnitId);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                facebookInterface.facebookStatus(true);
                inflateFixedFaceBookAd(activity, adType, (com.facebook.ads.NativeAd) ad, cardView);
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

    private static void inflateFixedFaceBookAd(Activity activity, NativeAdType adType, com.facebook.ads.NativeAd facebookAd, CardView adContainer) {
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


    //TODO: Interstitial Ads Implementation
    public static void showFixedInterstitialAd(Activity activity, AppInterfaces.InterstitialAdCallback interstitialAdCallback) {
        Constants.interstitialPlatformList.clear();
        Constants.interstitialPlatformList.addAll(Constants.platformList);
        Log.e(TAG, "Fixed Interstitial Ad Counter - "+fixedInterstitialAdCounter);
        if (fixedInterstitialAdCounter >= Constants.interstitialPlatformList.size()) {
            fixedInterstitialAdCounter = 0;
        }
        Log.e(TAG, "Fixed Interstitial Ad Counter - "+fixedInterstitialAdCounter);
        String tempAdPlatformType = Constants.interstitialPlatformList.get(fixedInterstitialAdCounter);
        if (tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
            showFixedFacebookInterstitialAd(activity, getInterstitialAdUnitId(tempAdPlatformType), new AppInterfaces.FacebookInterface() {
                @Override
                public void facebookStatus(boolean isLoaded) {
                    fixedInterstitialAdCounter++;
                    AdUtils.preCacheInterstitialAd = null;
                    if (!isLoaded) {
                        if (fixedInterstitialAdCounter < Constants.interstitialPlatformList.size()) {
                            showFixedInterstitialAd(activity, interstitialAdCallback);
                        } else {
                            Globals.hideLoadingDialog();
                            interstitialAdCallback.loadStatus(false);
                        }
                    } else {
                        AdUtils.adsHitCount = 0;
                        Globals.hideLoadingDialog();
                        interstitialAdCallback.loadStatus(true);
                    }
                }
            });
        }
        else {
            if (AdUtils.preCacheInterstitialAd == null) {
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity, getInterstitialAdUnitId(tempAdPlatformType), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // Interstitial ad is loaded.
                                loadFixedInterstitialAd(interstitialAd, interstitialAdCallback, activity);
                                fixedInterstitialAdCounter++;
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                fixedInterstitialAdCounter++;
                                if (fixedInterstitialAdCounter < Constants.interstitialPlatformList.size() - 1) {
                                    showFixedInterstitialAd(activity, interstitialAdCallback);
                                } else {
                                    Globals.hideLoadingDialog();
                                    interstitialAdCallback.loadStatus(false);
                                    Log.d(TAG + "Inter", "Interstitial Ad: Ads Failed to Load : " + loadAdError);
                                }
                            }
                        });
            } else {
                //Loading From PreCache
                loadFixedInterstitialAd(AdUtils.preCacheInterstitialAd, interstitialAdCallback, activity);
                Log.i(TAG + "Inter", "Interstitial Ad: Loaded From Precache");
            }
        }
    }

    private static void loadFixedInterstitialAd(@NonNull InterstitialAd interstitialAd, AppInterfaces.InterstitialAdCallback interstitialAdCallback, Activity activity) {
        //Interstitial Ad CallBack
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Interstitial Ad: Ad dismissed fullscreen content.");
                Globals.hideLoadingDialog();
                handler.post(() -> interstitialAdCallback.loadStatus(true));
                // interstitialAdCallback.loadStatus(true);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Interstitial Ad: Ad failed to show fullscreen content." + adError.getMessage());
                Globals.hideLoadingDialog();
                interstitialAdCallback.loadStatus(false);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                AdUtils.adsHitCount = 0;
                Globals.hideLoadingDialog();
            }
        });
        buildFixedInterstitialCache(activity);
        // Show Interstitial Ad
        interstitialAd.show(activity);
    }

    public static void buildFixedInterstitialCache(Activity activity) {
        AdUtils.preCacheInterstitialAd = null;
        if (Constants.IS_NETWORK_AVAILABLE && Constants.adsResponseModel.getPackage_name() != null) {
            String tempAdPlatformType = Constants.interstitialPlatformList.get(fixedInterstitialAdCounter);
            if (!tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
                AdRequest adRequest = new AdRequest.Builder().build();
                // "ca-app-pub-3940256099942544/1033173712" -> Test ID
                InterstitialAd.load(activity, getInterstitialAdUnitId(tempAdPlatformType), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                fixedInterstitialAdCounter++;
                                AdUtils.preCacheInterstitialAd = interstitialAd;
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
    }

    public static void showFixedFacebookInterstitialAd(Activity activity, String interstitialAdId, AppInterfaces.FacebookInterface facebookInterface) {
        com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(activity, interstitialAdId);
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.e(TAG + "Inter", "Facebook Interstitial Ad: Displayed ");

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.e(TAG + "Inter", "Facebook Interstitial Ad: Dismissed ");

            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                Log.e(TAG + "Inter", "Facebook Interstitial Ad: Error Occurred " + adError.getErrorMessage());
                Globals.hideLoadingDialog();
                facebookInterface.facebookStatus(false);

            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e(TAG + "Inter", "Facebook ad loaded");
                Globals.hideLoadingDialog();
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
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());

    }

    private static String getInterstitialAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getInterstitial_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getInterstitial_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getInterstitial_ads().getAdmob() : "";
    }


    //TODO: AppOpen Ads Implementation
    public static void showFixedAppOpenAd(Activity activity, AppInterfaces.AppOpenAdCallback appOpenAdCallback) {
        Constants.appOpenPlatformList.clear();
        Constants.appOpenPlatformList.addAll(Constants.platformList);
        Log.e(TAG, "Fixed AppOpen Ad Counter - "+fixedAppOpenAdCounter);
        if (fixedAppOpenAdCounter >= Constants.appOpenPlatformList.size()) {
            fixedAppOpenAdCounter = 0;
        }
        Log.e(TAG, "Fixed AppOpen Ad Counter - "+fixedAppOpenAdCounter);

        String tempAdPlatformType = Constants.appOpenPlatformList.get(fixedAppOpenAdCounter);
        tempAdPlatformType = tempAdPlatformType.equals(AdPlatform.Facebook.toString()) ? AdPlatform.Adx.toString() : tempAdPlatformType;
        if (AdUtils.preCacheAppOpenAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(activity, getAppOpenAdUnitId(tempAdPlatformType), adRequest, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d(TAG, "Fixed AppOpen Ad: Ads Failed to Load : "+loadAdError);
                    // Handle Failed To load
                    fixedAppOpenAdCounter++;
                    if (fixedAppOpenAdCounter < Constants.appOpenPlatformList.size() - 1) {
                        showFixedAppOpenAd(activity, appOpenAdCallback);
                    } else {
                        Globals.hideLoadingDialog();
                        appOpenAdCallback.loadStatus(false);
                    }
                }

                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    Log.d(TAG, "Fixed AppOpen Ad: Ads Success");
                    // Loading App Open from new request
                    fixedAppOpenAdCounter++;
                    loadFixedAppOpenAd(activity, appOpenAd, appOpenAdCallback);
                }
            });
        }
        else {
            //Loading From PreCache
            loadFixedAppOpenAd(activity, AdUtils.preCacheAppOpenAd, appOpenAdCallback);
            Log.i(TAG, "AppOpen Ad: Loaded From Precache");
        }
    }

    private static void loadFixedAppOpenAd(Activity activity, AppOpenAd appOpenAd, AppInterfaces.AppOpenAdCallback appOpenAdCallback) {
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                // AppOpen Dismissed
                Log.e(TAG, "AppOpen Ad: Dismissed");
                Globals.hideLoadingDialog();
                handler.postDelayed(() -> appOpenAdCallback.loadStatus(true), 25);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                // Handle Failed To Load
                Globals.hideLoadingDialog();
                appOpenAdCallback.loadStatus(false);
                Log.e(TAG, "AppOpen Ad:  onAdFailedToLoad: " + adError.getMessage());
            }
        };

        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
        // Building AppOpen AD Cache
        // buildFixedAppOpenCache(activity);
        // Show AppOpen Ad
        appOpenAd.show(activity);
    }

    public static void buildFixedAppOpenCache(Activity activity) {
        AdUtils.preCacheAppOpenAd = null;
        if (Constants.IS_NETWORK_AVAILABLE) {
            String tempAdPlatformType = Constants.appOpenPlatformList.get(fixedAppOpenAdCounter);
            if (!tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
                AdRequest adRequest = new AdRequest.Builder().build();
                AppOpenAd.load(activity, getAppOpenAdUnitId(tempAdPlatformType), adRequest, new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e(TAG, "AppOpen Ad:  onAdFailedToLoad: " + loadAdError.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        fixedAppOpenAdCounter++;
                        AdUtils.preCacheAppOpenAd = appOpenAd;
                        Log.e(TAG, "AppOpen Ad: AppOpen Ad PreCached");
                    }
                });
            }
        }
    }

    private static String getAppOpenAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getApp_open_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getApp_open_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getApp_open_ads().getAdmob() : "";
    }


    //TODO: Rewarded Ads Implementation
    public static void showFixedRewardedAd(Activity activity, AppInterfaces.RewardedAdCallback rewardedAdCallback) {
        Constants.rewardedPlatformList.clear();
        Constants.rewardedPlatformList.addAll(Constants.platformList);
        Log.e(TAG, "Fixed Rewarded Ad Counter - " + fixedRewardedAdCounter);
        if (fixedRewardedAdCounter >= Constants.rewardedPlatformList.size()) {
            fixedRewardedAdCounter = 0;
        }
        Log.e(TAG, "Fixed Rewarded Ad Counter - " + fixedRewardedAdCounter);

        if(Constants.IS_NETWORK_AVAILABLE) {
            String tempAdPlatformType = Constants.rewardedPlatformList.get(fixedRewardedAdCounter);
            tempAdPlatformType = tempAdPlatformType.equals(AdPlatform.Facebook.toString()) ? AdPlatform.Adx.toString() : tempAdPlatformType;
            if (AdUtils.preCacheRewardedAd == null) {
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(activity, getFixedRewardAdUnitId(tempAdPlatformType), adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        fixedRewardedAdCounter++;
                        if (fixedRewardedAdCounter < Constants.rewardedPlatformList.size() - 1) {
                            showFixedRewardedAd(activity, rewardedAdCallback);
                        } else {
                            Globals.hideLoadingDialog();
                            rewardedAdCallback.loadStatus(false);
                            Log.d(TAG, "Rewarded Ad: Ads Failed to Load : " + loadAdError);
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        fixedRewardedAdCounter++;
                        loadFixedRewardedAd(activity, rewardedAd, rewardedAdCallback);
                    }
                });
            } else {
                //Loading From PreCache
                Log.i(TAG, "Rewarded Ad: Loaded From Precache");
                loadFixedRewardedAd(activity, AdUtils.preCacheRewardedAd, rewardedAdCallback);
            }
        }
        else {
            Globals.hideLoadingDialog();
            rewardedAdCallback.loadStatus(false);
        }
    }

    private static void loadFixedRewardedAd(Activity activity, @NonNull RewardedAd rewardedAd, AppInterfaces.RewardedAdCallback rewardedAdCallback) {
        // Setting the fullscreen check callback for getting the successful response of the video
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Globals.hideLoadingDialog();
                rewardedAdCallback.loadStatus(false);
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Globals.hideLoadingDialog();
                handler.postDelayed(() -> rewardedAdCallback.loadStatus(true), 10); // This handler is the remove/avoid the black screen that comes after the ad
            }
        });
        // buildRewardedCache(activity, Constants.adsResponseModel.getRewarded_ads().getAdx()); // Loads & stores the next rewarded ad
        rewardedAd.show(activity, rewardItem -> {
            // rewardedAdCallback.loadStatus(true); // This callback automatically triggers when the reward video is finished. Can adjust this according to your requirement
        });
    }

    public static void buildFixedRewardedCache(Activity activity, String rewardedAdUnitId) {
        AdUtils.preCacheRewardedAd = null;
        if (Constants.IS_NETWORK_AVAILABLE) {
            String tempAdPlatformType = Constants.rewardedPlatformList.get(fixedRewardedAdCounter);
            if (!tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(activity, rewardedAdUnitId, adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "Rewarded Ad: Rewarded Ad Failure due to : " + loadAdError.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        // The AdUtils.preCacheRewardedAd value will be changing from NULL to the response value
                        fixedRewardedAdCounter++;
                        AdUtils.preCacheRewardedAd = rewardedAd;
                    }
                });
            }
        }
    }

    private static String getFixedRewardAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getRewarded_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getRewarded_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getRewarded_ads().getAdmob() : "";
    }


    //TODO: Banner Ads Implementation
    public static void showFixedBannerAd(Activity activity, MaterialCardView adContainer) {
        Constants.bannerPlatformList.clear();
        Constants.bannerPlatformList.addAll(Constants.platformList);
        Log.e(TAG + "ban", "Fixed Banner Ad Counter - " + fixedBannerAdCounter);
        if (fixedBannerAdCounter >= Constants.bannerPlatformList.size()) {
            fixedBannerAdCounter = 0;
        }
        Log.e(TAG + "ban", "Fixed Banner Ad Counter - " + fixedBannerAdCounter);

        String tempAdPlatformType = Constants.bannerPlatformList.get(fixedBannerAdCounter);
        if (tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, getFixedBannerAdUnitId(tempAdPlatformType), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adContainer.addView(adView);
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                   fixedBannerAdCounter++;
                    if (fixedBannerAdCounter < Constants.bannerPlatformList.size() - 1) {
                        showFixedBannerAd(activity, adContainer);
                    }
                    Log.e(TAG + "ban", "FaceBook Banner Ad: Error " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    fixedBannerAdCounter++;
                    Log.e(TAG + "ban", "FaceBook Banner Ad: Ad Loaded");
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
        } else {
            AdView bannerView = new AdView(activity);
            bannerView.setAdSize(AdSize.BANNER);
            bannerView.setAdUnitId(getFixedBannerAdUnitId(tempAdPlatformType));
            bannerView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    // Handle Error
                    fixedBannerAdCounter++;
                    if (fixedBannerAdCounter < Constants.platformList.size()-1) {
                        showFixedBannerAd(activity, adContainer);
                    }
                    Log.d(TAG + "ban", "BannerAd: onAdFailedToLoad: " + loadAdError.getMessage());
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    fixedBannerAdCounter++;
                    Log.d(TAG + "ban", "BannerAd: Ad Loaded");

                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Log.d(TAG + "ban", "BannerAd: Ad Opened");
                }
            });
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder(); // AdRequest Builder
            adContainer.addView(bannerView); // AdView is being added to the MaterialCardView which acts as container
            bannerView.loadAd(adRequestBuilder.build());
        }
    }

    private static String getFixedBannerAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getBanner_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getBanner_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getBanner_ads().getAdmob() : "";
    }


    //TODO: Collapsible Banner Ads Implementation
    public static void showFixedCollapsibleBannerAd(Activity activity, MaterialCardView adContainer) {
        if (Constants.IS_NETWORK_AVAILABLE) {
            Constants.collapsibleAdPlatformList.clear();
            Constants.collapsibleAdPlatformList.addAll(Constants.platformList);
            Log.e(TAG + "col", "Fixed Collapsible Ad Counter - " + fixedCollapsibleAdCounter);
            if (fixedCollapsibleAdCounter >= Constants.collapsibleAdPlatformList.size()) {
                fixedCollapsibleAdCounter = 0;
            }
            Log.e(TAG + "col", "Fixed Collapsible Ad Counter - " + fixedCollapsibleAdCounter);

            String tempAdPlatformType = Constants.collapsibleAdPlatformList.get(fixedCollapsibleAdCounter);
            if (tempAdPlatformType.equals(AdPlatform.Facebook.toString())) {
                fixedCollapsibleAdCounter = fixedCollapsibleAdCounter + 1 >= Constants.collapsibleAdPlatformList.size() ? 0 : fixedCollapsibleAdCounter + 1;
                tempAdPlatformType = Constants.collapsibleAdPlatformList.get(fixedCollapsibleAdCounter);
            }

            AdView bannerView = new AdView(activity);
            adContainer.addView(bannerView); // AdView is being added to the MaterialCardView which acts as container
            bannerView.setAdSize(AdUtils.getAdSize(activity, adContainer));
            bannerView.setAdUnitId(getFixedCollapsibleBannerAdUnitId(tempAdPlatformType));
            bannerView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    fixedCollapsibleAdCounter++;
                    if (fixedCollapsibleAdCounter < Constants.collapsibleAdPlatformList.size() - 1) {
                        showFixedCollapsibleBannerAd(activity, adContainer);
                    }
                    Log.d(TAG + "col", "CollapsibleBannerAd: onAdFailedToLoad: " + loadAdError.getMessage());
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    fixedCollapsibleAdCounter++;
                    Log.d(TAG + "col", "CollapsibleBannerAd: Ad Loaded");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Log.d(TAG + "col", "CollapsibleBannerAd: Ad Opened");
                }
            });
            Bundle extras = new Bundle();
            extras.putString("collapsible", "bottom");
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build(); // AdRequest Builder

            bannerView.loadAd(adRequest);
        } else {
            Log.d(TAG + "col", "CollapsibleBannerAd: No Internet");
        }
    }

    private static String getFixedCollapsibleBannerAdUnitId(String adTag) {
        return adTag.equals(AdPlatform.Adx.toString()) ? Constants.adsResponseModel.getCollapsible_ads().getAdx()
                : adTag.equals(AdPlatform.Facebook.toString()) ? Constants.adsResponseModel.getCollapsible_ads().getFacebook()
                : adTag.equals(AdPlatform.Admob.toString()) ? Constants.adsResponseModel.getCollapsible_ads().getAdmob() : "";
    }

}
