package com.kushyk.android.cleaner.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.text.DecimalFormat;
import java.util.Random;

import static com.kushyk.android.cleaner.activities.MainActivity.SAVED_STORAGE_OPTIMIZATION;
import static com.kushyk.android.cleaner.activities.MainActivity.isEnabled;
import static com.kushyk.android.cleaner.activities.MainActivity.numberOfOptimizations;
import static com.kushyk.android.cleaner.activities.MainActivity.sPref;
import static com.kushyk.android.cleaner.activities.SplashScreenActivity.isForeground;

public class StorageFragment extends Fragment {
    public static DecimalFormat df = new DecimalFormat("0.00");
    private InterstitialAd mInterstitialAd;

    private CircularProgressBar circularProgressBar;
    private TextView ramUsage1;
    private TextView ramUsage2;
    private TextView ram_percentage;
    private TextView total_proc;
    private TextView junkSizeTextView;
    private Button optimizeButton;
    private ObjectAnimator anim;
    private ObjectAnimator anim2;

    private double junk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.storage_screen, container, false);
        loadAd();
        setViews(rootView);
        getMemoryInfo();
        doButtonAnimation(optimizeButton, 1.1f, 0.9f, 1000);
        onOptimizedButtonClick();

        isOptimizationNeeded();
        return rootView;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void isOptimizationNeeded() {
        if (sPref.getBoolean(SAVED_STORAGE_OPTIMIZATION, false)) {
            anim.end();
            anim2.end();
            numberOfOptimizations++;
            optimizeButton.setText(R.string.optimized);
            optimizeButton.setBackground(getResources().getDrawable(R.drawable.rounded_corners_white_back));
            optimizeButton.setEnabled(false);
            circularProgressBar.setProgress(0);
            total_proc.setText(String.valueOf(generateRandomProc(10, 16)));
            junkSizeTextView.setText("0Mb");
        } else {
            setJunkSize();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onOptimizedButtonClick() {
        optimizeButton.setOnClickListener(view -> {
            anim.end();
            anim2.end();
            optimizeButton.setScaleX(1);
            optimizeButton.setScaleY(1);
            optimizeButton.setBackground(getResources().getDrawable(R.drawable.rounded_corners_white_back));
            optimizeButton.setText(R.string.scanning);
            circularProgressBar.setIndeterminateMode(true);
            animateTextView((float) junk, 0, junkSizeTextView, 14000, true);
            animateTextView(Integer.parseInt((String) total_proc.getText()), generateRandomProc(10, 16),
                    total_proc, 14000, false);

            new CountDownTimer(7000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    circularProgressBar.setIndeterminateMode(false);
                    circularProgressBar.setProgress(0);
                    total_proc.setText(String.valueOf(generateRandomProc(10, 16)));
                    optimizeButton.setText(R.string.optimized);
                    junkSizeTextView.setText("0Mb");
                    if (isForeground()) {
                        mInterstitialAd.show(requireActivity());
                    }
                    isEnabled = true;
                    numberOfOptimizations++;
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean(SAVED_STORAGE_OPTIMIZATION, true);
                    ed.apply();
                    startActivity(new Intent(getActivity(), ResultActivity.class));
                }
            }.start();
            optimizeButton.setEnabled(false);
            isEnabled = false;
        });
    }

    private void setViews(View rootView) {
        circularProgressBar = rootView.findViewById(R.id.circularProgressBar);
        circularProgressBar.setRoundBorder(true);
        junkSizeTextView = rootView.findViewById(R.id.junk_size_textView);
        ramUsage1 = rootView.findViewById(R.id.ramUsageTextView1);
        ramUsage2 = rootView.findViewById(R.id.ramUsageTextView2);
        ram_percentage = rootView.findViewById(R.id.ram_usage_percentage);
        optimizeButton = rootView.findViewById(R.id.optimizeButton);
        total_proc = rootView.findViewById(R.id.total_processes);
        total_proc.setText(String.valueOf(generateRandomProc(15, 57)));
    }

    private void setJunkSize() {
        junk = generateRandomProc(456, 1513);
        circularProgressBar.setProgressMax(100f);
        circularProgressBar.setProgressWithAnimation(Math.round(junk / 2000 * 100), 2000L);
        junkSizeTextView.setText((int) junk + "Mb");
    }

    private void getMemoryInfo() {
        ActivityManager actManager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        assert actManager != null;
        actManager.getMemoryInfo(memInfo);

        double totalMemory = memInfo.totalMem;
        double availMemory = memInfo.availMem;
        double usedMemory = totalMemory - availMemory;
        double precentlong = (((availMemory / totalMemory)) * 100);
        ramUsage1.setText(bytesToHuman(usedMemory) + "/" + bytesToHuman(totalMemory));
        ramUsage2.setText(bytesToHuman(usedMemory) + "/" + bytesToHuman(totalMemory));
        ram_percentage.setText(df.format(100 - precentlong) + "%");
    }

    private String floatForm(double d) {
        return String.format(java.util.Locale.US, "%.2f", d);
    }

    private String bytesToHuman(double size) {
        long Kb = 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size < Mb) return floatForm((double) size / Kb) + " KB";
        if (size < Gb) return floatForm((double) size / Mb) + " MB";
        return floatForm((double) size / Gb) + " GB";
    }

    private int generateRandomProc(int min, int max) {
        int diff = max - min;
        Random random = new Random();
        int i = random.nextInt(diff + 1);
        i += min;
        return i;
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

    public static void animateTextView(float initialValue, float finalValue, final TextView textview,
                                       int duration, boolean isMb) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(initialValue, finalValue);
        valueAnimator.setDuration(duration);
        if (isMb) {
            valueAnimator.addUpdateListener(valueAnimator1 ->
                    textview.setText(Math.round((Float) valueAnimator1.getAnimatedValue()) + "Mb"));
        } else {
            valueAnimator.addUpdateListener(valueAnimator1 ->
                    textview.setText(String.valueOf(Math.round((Float) valueAnimator1.getAnimatedValue()))));
        }

        valueAnimator.start();
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