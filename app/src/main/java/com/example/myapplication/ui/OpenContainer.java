package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.ui.fragment.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.myapplication.ui.fragment.HomeFragment;
import com.example.myapplication.ui.fragment.SettingsFragment;

public class OpenContainer extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static final String TAG = "OpenwrtContainer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openwrt_container);

        // 寻找获取底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            } else{
                selectedFragment = new DashboardFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}
