package com.seunghyun.randomseats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GridView seatsGrid;
    IndicatorSeekBar rowSeekBar, columnSeekBar;
    TextView settingLayoutTopTV, okButtonText;
    RelativeLayout okButton;
    LinearLayout settingLayout;
    IndicatorStayLayout indicatorLayout;

    ArrayList<Integer> seatList;
    TranslateAnimation outAnimation, inAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        makeGrid(SeatsGirdAdapter.INITIALIZE);

        final OnSeekChangeListener seekChangeListener = new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                makeGrid(SeatsGirdAdapter.INITIALIZE);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                makeGrid(SeatsGirdAdapter.INITIALIZE);
            }
        };
        rowSeekBar.setOnSeekChangeListener(seekChangeListener);
        columnSeekBar.setOnSeekChangeListener(seekChangeListener);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingLayout.startAnimation(outAnimation);
            }
        });

        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //settingLayout 이 화면에서 없어졌을 때
                indicatorLayout.setVisibility(View.GONE);
                settingLayoutTopTV.setText(getString(R.string.show_all_helper));
                okButtonText.setText(getString(R.string.show_all));
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeGrid(SeatsGirdAdapter.SHOW_ALL);
                    }
                });
                settingLayout.startAnimation(inAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void makeGrid(int type) {
        if (type == SeatsGirdAdapter.INITIALIZE) {
            //make list
            seatList = new ArrayList<>();
            int row = rowSeekBar.getProgress(), column = columnSeekBar.getProgress(), seats = row * column;
            for (int i = 1; i <= seats; i++) seatList.add(i);
            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + seats);
            seatsGrid.setNumColumns(column);
        }
        //Create grid
        seatsGrid.setAdapter(new SeatsGirdAdapter(getApplicationContext(), R.layout.grid_item, seatList, type));
    }

    private void initialize() {
        seatsGrid = findViewById(R.id.seats_grid);
        settingLayoutTopTV = findViewById(R.id.number_of_seats);
        okButton = findViewById(R.id.ok_button);
        okButtonText = findViewById(R.id.ok_button_text);
        settingLayout = findViewById(R.id.setting_layout);
        indicatorLayout = findViewById(R.id.indicator_layout);
        rowSeekBar = findViewById(R.id.row_seekBar);
        columnSeekBar = findViewById(R.id.column_seekBar);
        rowSeekBar.setIndicatorTextFormat(getString(R.string.row) + "${PROGRESS}");
        columnSeekBar.setIndicatorTextFormat(getString(R.string.column) + "${PROGRESS}");

        outAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.1f);
        outAnimation.setDuration(800);
        outAnimation.setFillAfter(true);
        inAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.1f,
                Animation.RELATIVE_TO_SELF, 0);
        inAnimation.setDuration(800);
        inAnimation.setFillAfter(true);
    }
}
