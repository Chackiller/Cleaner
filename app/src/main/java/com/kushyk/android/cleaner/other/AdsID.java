package com.kushyk.android.cleaner.other;

import androidx.annotation.NonNull;

public enum AdsID {
    TEST_AD_ID ("ca-app-pub-3940256099942544/1033173712");

    String id;

    AdsID(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return this.id;
    }
}
