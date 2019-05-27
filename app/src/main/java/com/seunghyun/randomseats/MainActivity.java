package com.seunghyun.randomseats;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener;
    private ViewPager viewPager;
    private ImageView backgroundImage;
    private ViewPager.OnPageChangeListener pageChangeListener;

    private DataViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataViewModel.updateIfRequire(this, false);

        //Init ad
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("F828F6F7909FEF842AC60DAF88B9DC16")
                .build();
        adView.loadAd(adRequest);

        model = ViewModelProviders.of(this).get(DataViewModel.class);

        init();
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(pageChangeListener);
        navigationView.setSelectedItemId(R.id.navigation_home);
        navigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        viewPager.setCurrentItem(1, false);
    }

    private void init() {
        navigationView = findViewById(R.id.nav_view);
        navigationItemSelectedListener = menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_history:
                    viewPager.setCurrentItem(0);
                    model.getCurrentPage().setValue(0);
                    return true;
                case R.id.navigation_home:
                    viewPager.setCurrentItem(1);
                    model.getCurrentPage().setValue(1);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(2);
                    model.getCurrentPage().setValue(2);
                    return true;
            }
            return false;
        };
        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                float value = (0.5f - v) * -2;
                if (i == 1) value = Math.abs(value);
                if (i == 1 && v > 0.5 && i1 > 0) value = 0;
                backgroundImage.setAlpha(value);
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        navigationView.setSelectedItemId(R.id.navigation_history);
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 2:
                        navigationView.setSelectedItemId(R.id.navigation_settings);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        };
        viewPager = findViewById(R.id.view_pager);
        backgroundImage = findViewById(R.id.background_image);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        private PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HistoryFragment();
                case 1:
                    return new HomeFragment();
                case 2:
                    return new SettingsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
