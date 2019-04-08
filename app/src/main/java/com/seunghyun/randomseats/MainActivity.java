package com.seunghyun.randomseats;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
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
    //numberList : seatList 에서 사용하지 않는 자리를 제외하고, 랜덤 숫자가 들어갈 리스트(사용하지 않는 자리에는 -1이 들어감) / arrayedNumberList : 정렬된 numberList
    ArrayList<Integer> seatList, shownSeatsList, exceptedList, numberList, arrayedNumberList;
    //fixedSeatsMap : 고정된 자리<Integer position, Integer number>
    SparseIntArray fixedSeatsMap;
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

        okButton.setOnClickListener(v -> settingLayout.startAnimation(outAnimation));

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

                    okButton.setOnClickListener(v -> {
                        makeGrid(SeatsGirdAdapter.SHOW_ALL);
                        seatsGrid.setOnItemClickListener(null);
                        settingLayout.startAnimation(outAnimation);
                        stage = 2;
                    });
                    seatsGrid.setOnItemClickListener((parent, view, position, id) -> {
                        if (!shownSeatsList.contains(position) && !exceptedList.contains(position)) //이미 있는 값이 아니고, 제외된 자리가 아니면
                            shownSeatsList.add(position);
                        if (shownSeatsList.size() == numberList.size() - exceptedList.size())
                            okButton.callOnClick();
                        else makeGrid(SeatsGirdAdapter.SHOW);
                    });

                    for (int i = 0, j = 1; j <= seatList.size() - exceptedList.size(); i++) {
                        if (exceptedList.contains(i)) numberList.add(-1); //제외된 자리라면 음수값 넣기
                        else {
                            numberList.add(j);
                            j++;
                        }
                    }
                    customShuffle(numberList);
                    settingLayout.startAnimation(inAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        seatsGrid.setOnItemClickListener((parent, view, position, id) -> {
            boolean isNotUseSeatChecked = exceptedList.contains(position);
            DetailSettingDialog dialog = new DetailSettingDialog(MainActivity.this, isNotUseSeatChecked, position, arrayedNumberList, fixedSeatsMap);
            dialog.show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe //from DetailSettingDialog
    public void onReceiveUseData(SendDialogUseEvent event) {
        if (event.isNotUseSeat) {
            exceptedList.add(event.position);
            seatsGrid.getChildAt(event.position).findViewById(R.id.grid_text).setBackgroundColor(Color.TRANSPARENT);
            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + (seatList.size() - exceptedList.size()));
        } else {
            exceptedList.remove(Integer.valueOf(event.position));
            seatsGrid.getChildAt(event.position).findViewById(R.id.grid_text).setBackgroundColor(Color.WHITE);
            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + (seatList.size() - exceptedList.size()));
        }
        arrayedNumberList.clear();
        for (int i = 1; i <= seatList.size() - exceptedList.size(); i++)
            arrayedNumberList.add(i);

        for (int i = 0; i < fixedSeatsMap.size(); i++) {
            int key = fixedSeatsMap.keyAt(i);
            int value = fixedSeatsMap.get(key);
            if (value > seatList.size() - exceptedList.size()) {
                fixedSeatsMap.delete(key);
                makeGrid(SeatsGirdAdapter.CHANGE);
            }
        }
    }

    @Subscribe //from DetailSettingDialog
    public void onChangedFixCheckBox(SendDialogFIxEvent event) {
        if (event.isFixSeat) fixedSeatsMap.put(event.position, event.number);
        else fixedSeatsMap.delete(event.position);
        makeGrid(SeatsGirdAdapter.CHANGE);
    }

    private void makeGrid(int type) {
        if (type == SeatsGirdAdapter.INITIALIZE) {
            seatList.clear();
            exceptedList.clear();
            arrayedNumberList.clear();
            fixedSeatsMap.clear();
            numberList.clear();
        }
        if (type == SeatsGirdAdapter.INITIALIZE || type == SeatsGirdAdapter.CHANGE) {
            //make list
            seatList = new ArrayList<>();
            arrayedNumberList = new ArrayList<>();
            int row = rowSeekBar.getProgress(), column = columnSeekBar.getProgress(), seats = row * column;
            for (int i = 1; i <= seats; i++) {
                seatList.add(i);
            }
            //Dialog spinner 에 들어갈 numberList 생성
            seats -= exceptedList.size();
            for (int i = 1; i <= seats; i++) {
                arrayedNumberList.add(i);
            }
            for (int i = 0; i < fixedSeatsMap.size(); i++) {
                int key = fixedSeatsMap.keyAt(i);
                arrayedNumberList.remove((Integer) fixedSeatsMap.get(key));
            }

            settingLayoutTopTV.setText(getString(R.string.number_of_seats) + seats);
            seatsGrid.setNumColumns(column);
        }
        //Create grid
        seatsGrid.setAdapter(new SeatsGirdAdapter(getApplicationContext(), R.layout.grid_item, seatList, type, exceptedList, shownSeatsList, numberList, fixedSeatsMap));
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
        arrayedNumberList = new ArrayList<>();
        fixedSeatsMap = new SparseIntArray();
    }

    private void customShuffle(ArrayList<Integer> list) {
        LinkedList<Integer> linkedList = new LinkedList<>(); //랜덤으로 섞으면 안되는 번호를 제외한 리스트
        ArrayList<Integer> mapValueList = new ArrayList<>(); //fixedSeatsMap 의 value 값만 모은 리스트
        for (int i = 0; i < fixedSeatsMap.size(); i++) {
            int key = fixedSeatsMap.keyAt(i);
            int value = fixedSeatsMap.get(key);
            mapValueList.add(value);
        }

        for (int value : list)
            if (value != -1 && !mapValueList.contains(value)) linkedList.add(value);
        Collections.shuffle(linkedList);

        for (int i = 0; i < list.size(); i++)
            if (list.get(i) != -1 && fixedSeatsMap.get(i) == 0) list.set(i, linkedList.pollLast());
        for (int i = 0; i < fixedSeatsMap.size(); i++) {
            int key = fixedSeatsMap.keyAt(i);
            int value = fixedSeatsMap.get(key);
            list.set(key, value);
        }
    }
}
