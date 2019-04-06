package com.seunghyun.randomseats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    GridView seatsGrid;
    IndicatorSeekBar rowSeekBar, columnSeekBar;
    TextView settingLayoutTopTV, okButtonText;
    RelativeLayout okButton;
    LinearLayout settingLayout;
    IndicatorStayLayout indicatorLayout;

    ArrayList<Integer> seatList, shownSeatsList;
    TranslateAnimation outAnimation, inAnimation;
    int stage = 1; //단계, 애니메이션 전환에 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        makeGrid(SeatsGirdAdapter.INITIALIZE, null);

        final OnSeekChangeListener seekChangeListener = new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                makeGrid(SeatsGirdAdapter.INITIALIZE, null);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                makeGrid(SeatsGirdAdapter.INITIALIZE, null);
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
                if (stage == 1) {
                    //settingLayout 이 화면에서 없어졌을 때 기능 전환
                    indicatorLayout.setVisibility(View.GONE);
                    settingLayoutTopTV.setText(getString(R.string.show_all_helper));
                    okButtonText.setText(getString(R.string.show_all));
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeGrid(SeatsGirdAdapter.SHOW_ALL, null);
                            seatsGrid.setOnItemClickListener(null);
                            settingLayout.startAnimation(outAnimation);
                            stage = 2;
                        }
                    });
                    Collections.shuffle(seatList);
                    settingLayout.startAnimation(inAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        seatsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shownSeatsList.add(position);
                makeGrid(SeatsGirdAdapter.SHOW, shownSeatsList);
            }
        });
    }

    private void makeGrid(int type, ArrayList<Integer> shownSeatsList) {
        if (type == SeatsGirdAdapter.INITIALIZE) {
            //make list
            seatList = new ArrayList<>();
            int row = rowSeekBar.getProgress(), column = columnSeekBar.getProgress(), seats = row * column;
            for (int i = 1; i <= seats; i++) seatList.add(i);
            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + seats);
            seatsGrid.setNumColumns(column);
        }
        //Create grid
        seatsGrid.setAdapter(new SeatsGirdAdapter(getApplicationContext(), R.layout.grid_item, seatList, type, shownSeatsList));
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
        outAnimation.setDuration(500);
        outAnimation.setFillAfter(true);
        inAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.1f,
                Animation.RELATIVE_TO_SELF, 0);
        inAnimation.setDuration(500);
        inAnimation.setFillAfter(true);

        seatList = new ArrayList<>();
        shownSeatsList = new ArrayList<>();
    }
}
