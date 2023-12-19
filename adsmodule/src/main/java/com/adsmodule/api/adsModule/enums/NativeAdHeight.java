package com.adsmodule.api.adsModule.enums;

public enum NativeAdHeight {
    SMALL (80),
    MEDIUM (132),
    FULL (250);


    private final int value;

    NativeAdHeight(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
