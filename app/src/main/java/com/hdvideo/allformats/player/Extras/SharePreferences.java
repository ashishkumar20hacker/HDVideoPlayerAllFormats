package com.hdvideo.allformats.player.Extras;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SharePreferences {

    private Context applicationContext;
    private Gson gson;
    private SharedPreferences sharedPreferences;
    private static final String LIST_KEY = "voiceEffectModelList";
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";
    private final String PREFERENCE_whatsapp = "whatsapp";
    private final String PREFERENCE_whatsappbusiness = "whatsappbusiness";

    public SharePreferences(Context applicationContext) {
        this.applicationContext = applicationContext;
        gson = new Gson();
        String preferencesName = applicationContext.getString(R.string.app_name);
        sharedPreferences = applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }
/*
    // Store a list of DataModels in SharedPreferences
    public void putDataModelList(List<AudioInfo> voiceEffectList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String dataModelListJson = gson.toJson(voiceEffectList);
        editor.putString(LIST_KEY, dataModelListJson);
        editor.apply();
    }

    // Retrieve the list of DataModels from SharedPreferences
    public List<AudioInfo> getDataModelList() {
        String dataModelListJson = sharedPreferences.getString(LIST_KEY, null);
        if (dataModelListJson != null) {
            Type type = new TypeToken<ArrayList<AudioInfo>>() {
            }.getType();
            return gson.fromJson(dataModelListJson, type);
        } else {
            return new ArrayList<>();
        }
    }*/

}
