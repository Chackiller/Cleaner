package com.kushyk.android.cleaner.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.kushyk.android.cleaner.other.AdsID;
import com.kushyk.android.cleaner.R;
import com.kushyk.android.cleaner.activities.ResultActivity;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import static android.content.Context.BATTERY_SERVICE;
import static com.kushyk.android.cleaner.activities.MainActivity.SAVED_BATTERY_OPTIMIZATION;
import static com.kushyk.android.cleaner.activities.MainActivity.isEnabled;
import static com.kushyk.android.cleaner.activities.MainActivity.numberOfOptimizations;
import static com.kushyk.android.cleaner.activities.MainActivity.sPref;
import static com.kushyk.android.cleaner.activities.SplashScreenActivity.isForeground;

public class BatteryFragment extends Fragment {
    private InterstitialAd mInterstitialAd;

    private ObjectAnimator anim;
    private ObjectAnimator anim2;
    private CircularProgressBar circularProgressBar;
    private TextView timeCharging;
    private Button optimizeButton;
    private int batteryLvl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.battery_screen, container, false);

        loadAd();
        getBatteryPercentage();
        setViews(rootView);
        isOptimizationNeeded(sPref.getBoolean(SAVED_BATTERY_OPTIMIZATION, false));
        onOptimizeButtonClick();

        return rootView;
    }

    private void setViews(View rootView) {
        circularProgressBar = rootView.findViewById(R.id.circularProgressBar);
        circularProgressBar.setRoundBorder(true);
        TextView batteryPrc = rootView.findViewById(R.id.battety_percTextView);
        timeCharging = rootView.findViewById(R.id.time_charging);
        circularProgressBar.setProgressWithAnimation(batteryLvl, 2000L);
        optimizeButton = rootView.findViewById(R.id.optimizeButton);
        batteryPrc.setText(batteryLvl + "%");
        setTimeCharging(0);
        doButtonAnimation(optimizeButton, 1.1f, 0.9f, 1000);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onOptimizeButtonClick() {
        optimizeButton.setOnClickListener(view -> {
            anim.end();
            anim2.end();
            optimizeButton.setScaleX(1);
            optimizeButton.setScaleY(1);
            optimizeButton.setBackground(getResources().getDrawable(R.drawable.rounded_corners_white_back));
            optimizeButton.setText(R.string.scanning);
            circularProgressBar.setIndeterminateMode(true);
            new CountDownTimer(7000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    circularProgressBar.setIndeterminateMode(false);
                    circularProgressBar.setProgress(0);
                    circularProgressBar.setProgressWithAnimation(batteryLvl, 1000L);
                    optimizeButton.setText(R.string.optimized);
                    setTimeCharging(40);
                    if (isForeground()) {
                        mInterstitialAd.show(requireActivity());
                    }
                    isEnabled = true;
                    numberOfOptimizations++;
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean(SAVED_BATTERY_OPTIMIZATION, true);
                    ed.apply();
                    startActivity(new Intent(getActivity(), ResultActivity.class));
                }
            }.start();
            optimizeButton.setEnabled(false);
            isEnabled = false;
        });
    }

    private void isOptimizationNeeded(boolean savedOptimization) {
        if (savedOptimization) {
            anim.end();
            anim2.end();
            numberOfOptimizations++;
            optimizeButton.setText(R.string.optimized);
            optimizeButton.setBackground(getResources().getDrawable(R.drawable.rounded_corners_white_back));
            optimizeButton.setEnabled(false);
            setTimeCharging(40);
        }
    }

    private void getBatteryPercentage() {
        BatteryManager bm = (BatteryManager) requireActivity().getSystemService(BATTERY_SERVICE);
        batteryLvl = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private void setTimeCharging(int time) {
        time = time + batteryLvl * 5;
        int hours = time / 60;
        int minutes = time % 60;
        timeCharging.setText(hours + "h " + minutes + "m");
    }

    public void doButtonAnimation(View button, float values, float values2, int duration) {
        anim = ObjectAnimator.ofFloat(button, "scaleX", values, values2);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();

        anim2 = ObjectAnimator.ofFloat(button, "scaleY", values, values2);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());
        anim2.setDuration(duration);
        anim2.setRepeatMode(ValueAnimator.REVERSE);
        anim2.setRepeatCount(ValueAnimator.INFINITE);
        anim2.start();
    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(requireContext(), AdsID.TEST_AD_ID.toString(), adRequest,
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
}