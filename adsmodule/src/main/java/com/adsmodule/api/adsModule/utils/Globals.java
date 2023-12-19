package com.adsmodule.api.adsModule.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.adsmodule.api.adsModule.R;
import com.adsmodule.api.adsModule.enums.NativeAdHeight;
import com.adsmodule.api.adsModule.enums.NativeAdType;
import com.google.android.gms.ads.MobileAds;

import java.util.List;
import java.util.Objects;

public class Globals {

    private static final String TAG = "Globals";
    public static Dialog loading_dialog;

    public static String getDeviceId(Context activity) {

        String device_id = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                device_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.e("getDeviceId: ", device_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                TelephonyManager tm = (TelephonyManager) activity.getSystemService(Activity.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return device_id;
                }
                if (tm != null && tm.getDeviceId() != null) {
                    device_id = tm.getDeviceId();
                    Log.e("getDeviceId: ", device_id);
                } else {
                    device_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                    Log.e("getDeviceId: ", device_id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return device_id;
    }

    public static boolean isListNull(List list) {
        try {
            if (list == null) {
                return true;
            } else return list.size() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNull(String val) {
        return val == null || val.trim().equalsIgnoreCase("") || val.trim().equalsIgnoreCase("null") || val.trim() == "" || val.trim() == "null";
    }


    public static boolean checkStringNull(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isConnectingToInternet(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

    public static void mobileAdsInitializer(Context activity) {
        MobileAds.initialize(activity);
    }

    public static void showLoadingDialog(Activity activity) {
        if (!activity.getComponentName().getClassName().equals("com.hdvideo.allformats.player.Activity.SplashActivity")) { // Change this classpath according to your SplashScreen Activity
            loading_dialog = new Dialog(activity);
            loading_dialog.setContentView(R.layout.loading_dialog);
            loading_dialog.setCancelable(false);
            loading_dialog.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(loading_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (!activity.isFinishing() && !loading_dialog.isShowing()) {
                Log.e(TAG, "showLoadingDialog: Dialog Shown");
                loading_dialog.show();
            } else loading_dialog.dismiss();
        }
    }

    public static void hideLoadingDialog() {
        if (loading_dialog != null && loading_dialog.isShowing()) loading_dialog.dismiss();
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public static void showUpdateAppDialog(Activity activity) {
        //TODO ForceUpdate functionality
        if(Constants.adsResponseModel != null){
            Dialog updateDialog = new Dialog(activity);
            updateDialog.setContentView(LayoutInflater.from(activity).inflate(R.layout.update_dialog, null));
            updateDialog.setCancelable(false);
            updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            updateDialog.findViewById(R.id.tv_install).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PLAYSTORE_BASE + activity.getPackageName())));
                }
            });
            updateDialog.show();
            Constants.adsResponseModel = null;
        }


    }

    public static boolean checkAppVersion(String apiVersionName, Activity activity) {
        String appVersionName = "";
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            appVersionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int appversion = Integer.parseInt(appVersionName.replace(".", ""));
        int apiversionname = Integer.parseInt(removeAlphabets(apiVersionName).replace(".", ""));
        return apiversionname > appversion;
    }
    private static String removeAlphabets(String apiVersionName) {
        return apiVersionName.replaceAll("[A-Za-z]", "");
    }

    public static void initOneSignal(Context context, String ONESIGNAL_APP_ID) {
        /*OneSignal.getDebug().setLogLevel(LogLevel.DEBUG);
        OneSignal.initWithContext(context, ONESIGNAL_APP_ID);
        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
            if (r.isSuccess()) {
                if (r.getData()) {
                    Toast.makeText(context, "requestPermission completed successfully and the user has accepted permission", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "requestPermission successfully but the user has rejected permission", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "requestPermission completed unsuccessfully, check `r.getThrowable()` for more info on the failure reason", Toast.LENGTH_SHORT).show();
            }
        }));*/
    }

    public static ImageView getAdPlaceholderImage(Activity activity, Drawable adPlaceholder, NativeAdType adType) {
        ImageView iv_placeholder = new ImageView(activity);
        // Changes Based On which layout is triggered
        if (adType.equals(NativeAdType.SMALL)) {
            iv_placeholder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) pxFromDp(activity, NativeAdHeight.SMALL.getValue()), Gravity.CENTER));
        } else if (adType.equals(NativeAdType.MEDIUM)) {
            iv_placeholder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) pxFromDp(activity, NativeAdHeight.MEDIUM.getValue()), Gravity.CENTER));
        } else {
            iv_placeholder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) pxFromDp(activity, NativeAdHeight.FULL.getValue()), Gravity.CENTER));
        }
        iv_placeholder.setImageDrawable(adPlaceholder);
        // Change AdPlaceHolder ScaleType From here
        iv_placeholder.setScaleType(ImageView.ScaleType.FIT_XY);
        return iv_placeholder;
    }

}
