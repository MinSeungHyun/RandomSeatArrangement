package com.seunghyun.randomseatarrangement;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener;
    private ViewPager viewPager;
    private ImageView backgroundImage;
    private ViewPager.OnPageChangeListener pageChangeListener;

    private DataViewModel model;
    private Menu mMenu;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.custom_actionbar, menu);
        Drawable drawable = menu.getItem(0).getIcon();
        drawable.mutate();
        drawable.setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_seat_appearance:
                int width = getResources().getDisplayMetrics().widthPixels;
                SeatAppearanceDialog dialog = new SeatAppearanceDialog(MainActivity.this, PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
                WindowManager.LayoutParams windowManager = Objects.requireNonNull(dialog.getWindow()).getAttributes();
                windowManager.copyFrom(dialog.getWindow().getAttributes());
                windowManager.width = (int) (width / 1.2);
                dialog.show();
                dialog.setOnDismissListener(dialog_ -> model.getIsSeatAppearanceSettingFinished().setValue(true));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        navigationView = findViewById(R.id.nav_view);
        navigationItemSelectedListener = menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_history:
                    setTitle(R.string.title_history);
                    viewPager.setCurrentItem(0);
                    model.getCurrentPage().setValue(0);
                    if (mMenu != null)
                        mMenu.findItem(R.id.action_seat_appearance).setVisible(false);
                    return true;
                case R.id.navigation_home:
                    setTitle(R.string.title_home);
                    viewPager.setCurrentItem(1);
                    model.getCurrentPage().setValue(1);
                    if (mMenu != null) mMenu.findItem(R.id.action_seat_appearance).setVisible(true);
                    return true;
                case R.id.navigation_settings:
                    setTitle(R.string.title_settings);
                    viewPager.setCurrentItem(2);
                    model.getCurrentPage().setValue(2);
                    if (mMenu != null)
                        mMenu.findItem(R.id.action_seat_appearance).setVisible(false);
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
