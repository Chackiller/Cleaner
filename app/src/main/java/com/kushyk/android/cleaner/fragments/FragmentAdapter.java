package com.kushyk.android.cleaner.fragments;

import android.widget.ImageButton;
import android.widget.TextView;

import com.kushyk.android.cleaner.fragments.BatteryFragment;
import com.kushyk.android.cleaner.fragments.StorageFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    private ImageButton storageButton;
    private ImageButton batteryButton;
    private TextView contentTextView;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                           ImageButton storageButton, ImageButton batteryButton, TextView contentTextView) {
        super(fragmentManager, lifecycle);
        this.storageButton = storageButton;
        this.batteryButton = batteryButton;
        this.contentTextView = contentTextView;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new BatteryFragment();
        }
        return new StorageFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
