package com.seunghyun.randomseats;

import android.graphics.Color;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    GridView seatsGrid;
    IndicatorSeekBar rowSeekBar, columnSeekBar;
    TextView settingLayoutTopTV, okButtonText;
    RelativeLayout okButton;
    LinearLayout settingLayout;
    IndicatorStayLayout indicatorLayout;

    //seatList : grid 를 생성하기 위한 리스트 / shownSeatsList : 숫자가 이미 보여진 자리 리스트 / exceptedList : 사용하지 않는 자리 리스트
    //numberList : seatList 에서 사용하지 않는 자리를 제외하고, 랜덤 숫자가 들어갈 리스트
    ArrayList<Integer> seatList, shownSeatsList, exceptedList, numberList, arrayedNumberList;
    TranslateAnimation outAnimation, inAnimation;
    int stage = 1; //단계, 애니메이션 전환에 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
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
                if (stage == 1) {
                    //settingLayout 이 화면에서 없어졌을 때 기능 전환
                    indicatorLayout.setVisibility(View.GONE);
                    settingLayoutTopTV.setText(getString(R.string.show_all_helper));
                    okButtonText.setText(getString(R.string.show_all));

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeGrid(SeatsGirdAdapter.SHOW_ALL);
                            seatsGrid.setOnItemClickListener(null);
                            settingLayout.startAnimation(outAnimation);
                            stage = 2;
                        }
                    });
                    seatsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (!shownSeatsList.contains(position) && !exceptedList.contains(position)) //이미 있는 값이 아니고, 제외된 자리가 아니면
                                shownSeatsList.add(position);
                            if (shownSeatsList.size() == numberList.size() - exceptedList.size())
                                okButton.callOnClick();
                            else makeGrid(SeatsGirdAdapter.SHOW);
                        }
                    });

                    for (int i = 0, j = 1; j <= seatList.size() - exceptedList.size(); i++) {
                        if (exceptedList.contains(i)) numberList.add(-1); //제외된 자리라면 음수값 넣기
                        else {
                            numberList.add(j);
                            j++;
                        }
                    }
                    shuffleExceptMinus(numberList);
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
                boolean isNotUseSeatChecked = exceptedList.contains(position);
                DetailSettingDialog dialog = new DetailSettingDialog(MainActivity.this, isNotUseSeatChecked, position, arrayedNumberList);
                dialog.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe //from DetailSettingDialog
    public void onReceiveData(SendDialogDataEvent event) {
        if (event.isNotUseSeat) {
            exceptedList.add(event.position);
            seatsGrid.getChildAt(event.position).findViewById(R.id.grid_text).setBackgroundColor(Color.TRANSPARENT);
            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + (seatList.size() - exceptedList.size()));
        } else {
            exceptedList.remove(Integer.valueOf(event.position));
            seatsGrid.getChildAt(event.position).findViewById(R.id.grid_text).setBackgroundColor(Color.WHITE);
            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + (seatList.size() - exceptedList.size()));
        }
    }

    private void makeGrid(int type) {
        if (type == SeatsGirdAdapter.INITIALIZE) {
            //make list
            seatList = new ArrayList<>();
            int row = rowSeekBar.getProgress(), column = columnSeekBar.getProgress(), seats = row * column;
            for (int i = 1; i <= seats; i++) seatList.add(i);
            seats -= exceptedList.size();
            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + seats);
            seatsGrid.setNumColumns(column);
            //remove needless exceptSeat
            for (int exceptedSeat : exceptedList)
                if (exceptedSeat > seats) exceptedList.remove(Integer.valueOf(exceptedSeat));
        }
        //Create grid
        seatsGrid.setAdapter(new SeatsGirdAdapter(getApplicationContext(), R.layout.grid_item, seatList, type, exceptedList, shownSeatsList, numberList));
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
        exceptedList = new ArrayList<>();
        numberList = new ArrayList<>();
    }

    private void shuffleExceptMinus(ArrayList<Integer> list) {
        LinkedList<Integer> listExceptedMinus = new LinkedList<>();
        for (int value : list)
            if (value >= 0) listExceptedMinus.add(value);
        Collections.shuffle(listExceptedMinus);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != -1) list.set(i, listExceptedMinus.pollLast());
        }
    }
}
