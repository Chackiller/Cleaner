package com.kushyk.android.cleaner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.kushyk.android.cleaner.other.AdsID;
import com.kushyk.android.cleaner.R;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class SplashScreenActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    private long ticks = 8500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_splash_screen);
        TextView privacyTextLink = findViewById(R.id.privacy_textbutton);
        privacyTextLink.setMovementMethod(LinkMovementMethod.getInstance());

        loadAd();
        doTimer(ticks);

    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, AdsID.TEST_AD_ID.toString(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i("adError", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void makeFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void doTimer(long time) {
        new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                ticks = l;
            }

            @Override
            public void onFinish() {
                if (mInterstitialAd != null) {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                    if (isForeground())
                        mInterstitialAd.show(SplashScreenActivity.this);

                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }.start();
    }

    public static boolean isForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }


}