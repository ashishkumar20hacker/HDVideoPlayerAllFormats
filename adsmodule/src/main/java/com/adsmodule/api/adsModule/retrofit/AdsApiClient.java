package com.adsmodule.api.adsModule.retrofit;

import com.facebook.ads.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdsApiClient {
    private static AdsApiClient adsApiClient;
    private Retrofit retrofit = null;

    public static AdsApiClient getInstance() {
        if (adsApiClient == null) {
            adsApiClient = new AdsApiClient();
        }
        return adsApiClient;
    }

    public Retrofit getClient(String baseURL) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);   // development build
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);    // production build
        }
        client.addInterceptor(interceptor);
        client.readTimeout(60, TimeUnit.SECONDS);
        client.writeTimeout(60, TimeUnit.SECONDS);
        client.connectTimeout(60, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder().baseUrl(baseURL).client(client.build()).addConverterFactory(GsonConverterFactory.create()).build();

        return retrofit;
    }
}

