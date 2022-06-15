package com.kushyk.android.cleaner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kushyk.android.cleaner.R;
import com.kushyk.android.cleaner.fragments.FragmentAdapter;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sPref;
    public static final String SAVED_BATTERY_OPTIMIZATION = "saved_battery_optimization";
    public static final String SAVED_STORAGE_OPTIMIZATION = "saved_storage_optimization";
    public static final String SAVED_FIRST_DATE_BACKGROUND_MONEY = "first_date_background";

    private ImageButton batteryButton;
    private ImageButton storageButton;
    private TextView contentTextView;

    public static boolean isEnabled = true;
    public static ViewPager2 mViewPager2;
    public static int numberOfOptimizations = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeFullScreen();
        sPref = getPreferences(MODE_PRIVATE);
        isOptimizationNeeded();

        setContentView(R.layout.activity_main);
        setViews();
        setViewPager();
        showTopAddBanner();
    }

    private void setViews() {
        batteryButton = findViewById(R.id.batteryButton);
        storageButton = findViewById(R.id.storageButton);
        contentTextView = findViewById(R.id.content_textview);
        mViewPager2 = findViewById(R.id.viewPager2);
        batteryButton.setScaleX(0.5f);
        batteryButton.setScaleY(0.5f);
    }

    private void setViewPager() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentAdapter fragmentAdapter = new FragmentAdapter(fm, getLifecycle(), storageButton, batteryButton, contentTextView);
        mViewPager2.setAdapter(fragmentAdapter);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    contentTextView.setText(R.string.battery_saver);
                    onButtonClickScaleAnimation(batteryButton, 0.8f, 600);
                    onButtonClickScaleAnimation(storageButton, 0.3f, 600);
                } else {
                    contentTextView.setText(R.string.charge_booster);
                    onButtonClickScaleAnimation(storageButton,0.65f, 600);
                    onButtonClickScaleAnimation(batteryButton,0.5f, 600);
                }
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

    private void showTopAddBanner() {
        MobileAds.initialize(this, initializationStatus -> {});

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void onStorageButtonClick(View view) {
        mViewPager2.setCurrentItem(0);
        contentTextView.setText(R.string.charge_booster);
        onButtonClickScaleAnimation(storageButton,0.65f, 600);
        onButtonClickScaleAnimation(batteryButton,0.5f, 600);
    }

    public void onBatteryButtonClick(View view) {
        mViewPager2.setCurrentItem(1);
        contentTextView.setText(R.string.battery_saver);
        onButtonClickScaleAnimation(batteryButton,0.8f, 600);
        onButtonClickScaleAnimation(storageButton,0.3f, 600);
    }

    public static void onButtonClickScaleAnimation(View button, float values, int duration) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(button, "scaleX", values);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);
        anim.start();

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(button, "scaleY", values);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());
        anim2.setDuration(duration);
        anim2.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isEnabled)
        return super.dispatchTouchEvent(ev);
        return false;
    }

    private void isOptimizationNeeded() {
        long millisecondsInBackground = sPref.getLong(SAVED_FIRST_DATE_BACKGROUND_MONEY,
                Calendar.getInstance().getTimeInMillis());
        double timeInBackground = (double) (Calendar.getInstance().getTimeInMillis()
                - millisecondsInBackground) / 1000 / 60 / 60;
        if (timeInBackground >= 2) {
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean(SAVED_BATTERY_OPTIMIZATION, false);
            ed.putBoolean(SAVED_STORAGE_OPTIMIZATION, false);
            ed.apply();
        }
    }
}