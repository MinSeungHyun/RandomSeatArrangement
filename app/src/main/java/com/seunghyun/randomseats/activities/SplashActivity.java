package com.seunghyun.randomseats.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.seunghyun.randomseats.R;

public class SplashActivity extends AppCompatActivity {

    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onStart() {
        super.onStart();
        isRunning = true;
        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        isRunning = false;
    }

    private class splashHandler implements Runnable {
        public void run() {
            if (isRunning) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }
    }
}
