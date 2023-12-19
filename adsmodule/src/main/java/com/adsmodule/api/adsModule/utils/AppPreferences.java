package com.adsmodule.api.adsModule.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {
    private final Context context;
    private final SharedPreferences sharedPreferences;

    public AppPreferences(Context context, int prefsNameResourceID) {
        this.context = context;
        String preferencesName = context.getString(prefsNameResourceID);
        sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    public boolean isAdsFirstRun() {
        return getBoolean(Constants.IS_ADS_FIRST_RUN, true);
    }

    public void setAdsFirstRun(boolean isFirstRun) {
        putBoolean(Constants.IS_ADS_FIRST_RUN, isFirstRun);
    }

    public boolean isAppFirstRun() {
        return getBoolean(Constants.IS_APP_FIRST_RUN, false);
    }

    public void setAppFirstRun(boolean isFirstRun) {
        putBoolean(Constants.IS_APP_FIRST_RUN, isFirstRun);
    }

    public String getAdsBaseUrl() {
        return getString(Constants.STORED_BASE_URL, Constants.IDE_BASE_URL); // Change the Default BASE URL according to your panel
    }

    public void setAdsBaseUrl(String baseurl) {
        putString(Constants.STORED_BASE_URL, baseurl);
    }


    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }


    public void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
