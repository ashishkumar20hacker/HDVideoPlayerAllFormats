package com.adsmodule.api.adsModule.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adsmodule.api.adsModule.R;
import com.adsmodule.api.adsModule.enums.NativeAdType;
import com.adsmodule.api.adsModule.utils.AdUtils;
import com.google.android.material.card.MaterialCardView;

public class NativeRecyclerView extends MaterialCardView {

    private static final String TAG = "NativeView";

    private Context context;
    int borderColor;
    Drawable adPlaceHolder;
    NativeAdType adType;

    public NativeRecyclerView(@NonNull Context context) {
        super(context);
    }

    public NativeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NativeView, 0, 0);
        this.context = context;

        int borderColor;
        Drawable adPlaceHolder;
        NativeAdType adType;
        try {
            adType = NativeAdType.values()[array.getInt(R.styleable.NativeView_adType, 0)];
            borderColor = array.getColor(R.styleable.NativeView_borderColor, Color.TRANSPARENT);
            adPlaceHolder = array.getDrawable(R.styleable.NativeView_placeholder);
            this.borderColor = borderColor;
            this.adPlaceHolder = adPlaceHolder;
            this.adType = adType;
        } finally {
            array.recycle();
        }
        this.setRadius(30f);
        this.setStrokeWidth(2);
        this.setStrokeColor(borderColor);

    }
    
    public void checkAndLoadAd(boolean loadAd, Activity activity, NativeAdType adType, Drawable adPlaceHolder) {
        if (loadAd) {
            AdUtils.showNativeAd(activity, this, adType, adPlaceHolder);
        }
    }

}
