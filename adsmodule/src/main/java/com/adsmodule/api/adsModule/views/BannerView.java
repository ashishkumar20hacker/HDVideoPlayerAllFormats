package com.adsmodule.api.adsModule.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.adsmodule.api.adsModule.R;
import com.adsmodule.api.adsModule.utils.AdUtils;
import com.adsmodule.api.adsModule.utils.Constants;
import com.google.android.material.card.MaterialCardView;


public class BannerView extends MaterialCardView {

    boolean isCollapsible = false;
    TypedArray typedArray;

    public BannerView(@NonNull Context context) {
        super(context);
    }

    public BannerView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);

        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BannerView, 0, 0);

        try {
            isCollapsible = typedArray.getBoolean(R.styleable.BannerView_isCollapsible, false);
        } finally {
            typedArray.recycle();
        }

        this.setRadius(0f);
        this.setStrokeWidth(0);
        this.setCardElevation(0f);
        if (isCollapsible)
            AdUtils.showCollapsibleBannerAd((Activity) context, this);
        else
            AdUtils.showBannerAd((Activity) context, this);
    }


}
