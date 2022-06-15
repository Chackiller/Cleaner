package com.kushyk.android.cleaner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kushyk.android.cleaner.R;

import static com.kushyk.android.cleaner.activities.MainActivity.mViewPager2;
import static com.kushyk.android.cleaner.activities.MainActivity.numberOfOptimizations;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_result);

        showBottomAddBanner();
        Button doneButton = findViewById(R.id.done_button);
        TextView doneText = findViewById(R.id.done_textView);
        setViews(doneButton, doneText);

        onDoneButtonClickListener(doneButton);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setViews(Button doneButton, TextView doneText) {
        if (numberOfOptimizations == 1) {
            doneText.setText("1/2 " + getResources().getString(R.string.optimized));
            doneButton.setText(R.string.continue_optimization);
        } else {
            doneText.setText(R.string.optimized);
            doneButton.setText(R.string.done);
            doneButton.setBackground(getDrawable(R.drawable.rounded_corners_white_back));
        }
    }

    private void onDoneButtonClickListener(Button doneButton) {
        doneButton.setOnClickListener(view -> {
            if(mViewPager2.getCurrentItem() == 1) {
                mViewPager2.setCurrentItem(0);
            } else {
                mViewPager2.setCurrentItem(1);
            }
            finish();
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

    private void showBottomAddBanner() {
        MobileAds.initialize(this, initializationStatus -> {});

        AdView adView = findViewById(R.id.adViewBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}