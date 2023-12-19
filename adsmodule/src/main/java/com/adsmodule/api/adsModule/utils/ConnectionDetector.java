package com.adsmodule.api.adsModule.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class ConnectionDetector extends LiveData<Boolean> {

    private final ConnectivityManager cm;
    private final ConnectivityManager.NetworkCallback networkCallback;

    public ConnectionDetector(Application application) {
        this.cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                postValue(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                postValue(false);
            }

            @Override
            public void onLosing(@NonNull Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
                postValue(false);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                postValue(false);
            }
        };
    }

    @Override
    protected void onActive() {
        super.onActive();
        NetworkRequest request = new NetworkRequest.Builder().build();
        cm.registerNetworkCallback(request, networkCallback);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        cm.unregisterNetworkCallback(networkCallback);
    }
}