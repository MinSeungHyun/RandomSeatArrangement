package com.seunghyun.randomseatarrangement;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private final int RESET_COUNT_TO_SHOW_REVIEW = 5;

    private IndicatorSeekBar rowSeekBar, columnSeekBar;
    private IndicatorStayLayout indicatorStayLayout;
    private GridLayout seatsGridLayout, verticalNumberGridLayout, horizontalNumberGridLayout;
    private TextView stageButton, seatsNumberTV, cautionTV;
    private LinearLayout bottomSheet, homeFragmentContainer;
    private ScrollView seatsGridScrollView;
    private HorizontalScrollView seatsGridHorizontalScrollView, horizontalNumberScrollView;
    private ViewGroup parent;
    private ImageView stageImageView;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private BottomSheetBehavior bottomSheetBehavior;
    private OnSeekChangeListener seekChangeListener;
    private DataViewModel model;
    private SharedPreferences preference;
    private SharedPreferences.Editor preferenceEditor;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private boolean isChangingTrigger = false; //onStageButtonClickListener 와 bottomSheetCallback 간의 통신을 위함
    private int row, column;
    private float mX = 0;
    private boolean isNotFirstTouch = true;
    private String rowSeatNumberType, columnSeatNumberType;
    private ArrayList<String> notUseSeatTags;
    private HashMap<String, String> fixedSeatsMap;
    private InterstitialAd interstitialAd;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        interstitialAd = new InterstitialAd(requireActivity());
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        preference = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
        preferenceEditor = preference.edit();
        preferenceEditor.apply();
        model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DataViewModel.class);
        final Observer<Integer> currentPageObserver = currentPage -> {
            if (currentPage != null && currentPage == 1)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        };
        model.getCurrentPage().observe(this, currentPageObserver);

        if (model.getIsChangingStage().getValue() == null)
            model.getIsChangingStage().setValue(false);
        if (model.getCurrentStage().getValue() == null)
            model.getCurrentStage().setValue(1);
        final Observer<Boolean> isChangingStageObserver = isChangingStage -> {
            if (isChangingStage != null && model.getCurrentStage().getValue() != null
                    && isChangingStage) {
                switch (model.getCurrentStage().getValue()) {
                    case 1:
                        //자리 설정 단계 -> 자리 확인 단계
                        seatsNumberTV.setVisibility(View.GONE);
                        indicatorStayLayout.setVisibility(View.GONE);
                        cautionTV.setText(R.string.show_all_helper);
                        cautionTV.setTextColor(requireActivity().getColor(R.color.colorPrimary));
                        cautionTV.setTypeface(null, Typeface.BOLD);
                        cautionTV.setVisibility(View.VISIBLE);
                        makeRandomList();
                        stageButton.setText(getString(R.string.show_all));
                        model.getCurrentStage().setValue(2);
                        break;
                    case 2:
                        //자리 확인 단계 -> 초기화 단계
                        showAll();
                        cautionTV.setVisibility(View.GONE);
                        stageButton.setText(getString(R.string.reset));
                        stageImageView.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_baseline_replay_24px));
                        model.getCurrentStage().setValue(3);
                        break;
                    case 3:
                        //초기화 단계 -> 자리 설정 단계
                        reset();
                        seatsNumberTV.setVisibility(View.VISIBLE);
                        indicatorStayLayout.setVisibility(View.VISIBLE);
                        cautionTV.setText(R.string.caution);
                        cautionTV.setTextColor(requireActivity().getColor(android.R.color.darker_gray));
                        cautionTV.setTypeface(null, Typeface.NORMAL);
                        cautionTV.setVisibility(View.VISIBLE);
                        stageButton.setText(getString(R.string.next));
                        stageImageView.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_baseline_check_24px));
                        model.getCurrentStage().setValue(1);
                        break;
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                model.getIsChangingStage().setValue(false);
            }
        };
        model.getIsChangingStage().observe(this, isChangingStageObserver);

        if (model.getNotUseSeatTags().getValue() == null)
            model.getNotUseSeatTags().setValue(new ArrayList<>());
        notUseSeatTags = model.getNotUseSeatTags().getValue();
        final Observer<ArrayList<String>> notUseSeatTagsListener = notUseSeatTags -> {
            updateNotUseSeat();
            this.notUseSeatTags = notUseSeatTags;
        };
        model.getNotUseSeatTags().observe(this, notUseSeatTagsListener);

        if (model.getFixedSeatsMap().getValue() == null)
            model.getFixedSeatsMap().setValue(new HashMap<>());
        fixedSeatsMap = model.getFixedSeatsMap().getValue();
        final Observer<HashMap<String, String>> fixedSeatsMapListener = fixedSeatsMap -> {
            this.fixedSeatsMap = fixedSeatsMap;
            updateFixedSeats();
        };
        model.getFixedSeatsMap().observe(this, fixedSeatsMapListener);

        model.getIsSeatAppearanceSettingFinished().observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                makeGrid();
                updateState();
                model.getIsSeatAppearanceSettingFinished().setValue(false);
            }
        });

        rowSeatNumberType = preference.getString(getString(R.string.row_seat_number_key), getString(R.string.alphabet));
        columnSeatNumberType = preference.getString(getString(R.string.column_seat_number_key), getString(R.string.number));
        preferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals(getString(R.string.use_seatNumber_key))) {
                makeGrid();
                updateState();
            } else if (key.equals(getString(R.string.row_seat_number_key))) {
                rowSeatNumberType = preference.getString(key, getString(R.string.alphabet));
                makeGrid();
                updateState();
            } else if (key.equals(getString(R.string.column_seat_number_key))) {
                columnSeatNumberType = preference.getString(key, getString(R.string.number));
                makeGrid();
                updateState();
            } else if (key.equals(getString(R.string.reset_count_key))) {
                if (preference.getInt(key, 0) == RESET_COUNT_TO_SHOW_REVIEW) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                    dialog.setTitle(R.string.review_title);
                    dialog.setMessage(R.string.review_message);
                    dialog.setPositiveButton(R.string.yes, (dialog1, which) -> DataViewModel.makeReview(requireActivity()));
                    dialog.setNeutralButton(R.string.no, ((dialog1, which) -> Toast.makeText(requireActivity(), R.string.review_no, Toast.LENGTH_LONG).show()));
                    dialog.show();
                }
            }
        };
        preference.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        parent = container;
        init(view);
        rowSeekBar.setOnSeekChangeListener(seekChangeListener);
        columnSeekBar.setOnSeekChangeListener(seekChangeListener);
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        stageButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        makeGrid();
        updateState();

        new WaitViewsToBeShown().start();
        setGridLayoutMargin();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preference.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(View view) {
        seatsGridScrollView = view.findViewById(R.id.seats_scrollView);
        seatsGridHorizontalScrollView = view.findViewById(R.id.seats_horizontal_scrollView);
        rowSeekBar = view.findViewById(R.id.row_seekBar);
        columnSeekBar = view.findViewById(R.id.column_seekBar);
        rowSeekBar.setIndicatorTextFormat(getString(R.string.row) + "${PROGRESS}");
        columnSeekBar.setIndicatorTextFormat(getString(R.string.column) + "${PROGRESS}");
        seatsGridLayout = view.findViewById(R.id.seats_gridLayout);
        verticalNumberGridLayout = view.findViewById(R.id.vertical_number_gridLayout);
        horizontalNumberGridLayout = view.findViewById(R.id.horizontal_number_gridLayout);
        stageButton = view.findViewById(R.id.stage_button);
        seatsNumberTV = view.findViewById(R.id.number_of_seats_TV);
        indicatorStayLayout = view.findViewById(R.id.indicator_layout);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        stageImageView = view.findViewById(R.id.stage_image);
        horizontalNumberScrollView = view.findViewById(R.id.horizontal_number_scrollView);
        homeFragmentContainer = view.findViewById(R.id.fragment_home_container);
        homeFragmentContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        cautionTV = view.findViewById(R.id.caution_text);

        seekChangeListener = new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                model.getNotUseSeatTags().setValue(new ArrayList<>());
                model.getFixedSeatsMap().setValue(new HashMap<>());
                makeGrid();
            }
        };

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (isChangingTrigger && i == BottomSheetBehavior.STATE_COLLAPSED) {
                    model.getIsChangingStage().setValue(true);
                    isChangingTrigger = false;
                }
                homeFragmentContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING); //Enable animation when dragging stopped
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                final ImageView dragImage = view.findViewById(R.id.drag_image);
                Drawable background = dragImage.getBackground();
                background.setAlpha((int) (v * 255));
                setGridLayoutMargin();
                homeFragmentContainer.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGING);
            }
        };

        seatsGridScrollView.setOnTouchListener((v, event) -> {
            float curX;
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    curX = event.getX();
                    if (isNotFirstTouch) {
                        int x = (int) ((mX - curX) * 1.2);
                        seatsGridHorizontalScrollView.scrollBy(x, 0);
                    }
                    mX = curX;
                    isNotFirstTouch = true;
                    break;
                case MotionEvent.ACTION_UP:
                    isNotFirstTouch = false;
                    break;
            }
            return false;
        });

        horizontalNumberScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> seatsGridHorizontalScrollView.setScrollX(scrollX));
        seatsGridHorizontalScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> horizontalNumberScrollView.setScrollX(scrollX));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage_button:
                isChangingTrigger = true;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); //go to isChangingStageObserver
                break;
            case R.id.item_text:
                final String tag = v.getTag().toString();
                if (model.getCurrentStage().getValue() != null) {
                    switch (model.getCurrentStage().getValue()) {
                        case 1:
                            //자리 설정 단계일 때
                            final SimpleTooltip tooltip = new SimpleTooltip.Builder(getContext())
                                    .anchorView(parent.findViewWithTag(tag))
                                    .text("test")
                                    .gravity(Gravity.TOP)
                                    .contentView(R.layout.seat_detail_setting_layout)
                                    .showArrow(true)
                                    .arrowColor(Objects.requireNonNull(getActivity()).getColor(R.color.transparentWhite))
                                    .arrowHeight(50)
                                    .arrowWidth(50)
                                    .dismissOnInsideTouch(false)
                                    .build();

                            final CheckBox useSeatCheckBox = tooltip.findViewById(R.id.use_seat_checkBox);
                            final Spinner fixNumberSpinner = tooltip.findViewById(R.id.fix_seat_spinner);
                            fixNumberSpinner.setLayoutMode(Spinner.MODE_DIALOG);
                            if (notUseSeatTags.contains(tag)) {
                                useSeatCheckBox.setChecked(true);
                                fixNumberSpinner.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.disabledBlack));
                                fixNumberSpinner.setEnabled(false);
                                initFIxNumberSpinner(fixNumberSpinner, tag, R.layout.custom_spinner_dropdown_item_spinner_disabled);
                            } else {
                                useSeatCheckBox.setChecked(false);
                                fixNumberSpinner.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), android.R.color.black));
                                fixNumberSpinner.setEnabled(true);
                                initFIxNumberSpinner(fixNumberSpinner, tag, R.layout.custom_spinner_dropdown_item);
                            }
                            useSeatCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                int spinnerItem;
                                if (isChecked) {
                                    notUseSeatTags.add(tag);
                                    fixNumberSpinner.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.disabledBlack));
                                    spinnerItem = R.layout.custom_spinner_dropdown_item_spinner_disabled;
                                    fixNumberSpinner.setEnabled(false);
                                } else {
                                    notUseSeatTags.remove(tag);
                                    fixNumberSpinner.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), android.R.color.black));
                                    spinnerItem = R.layout.custom_spinner_dropdown_item;
                                    fixNumberSpinner.setEnabled(true);
                                }
                                model.getNotUseSeatTags().setValue(notUseSeatTags);
                                initFIxNumberSpinner(fixNumberSpinner, tag, spinnerItem);
                            });
                            tooltip.show();
                            break;
                        case 2:
                            //자리 확인 단계일 때
                            showSeat(tag);
                            ArrayList<String> shownSeatsList = model.getShownSeatsList().getValue() == null ? new ArrayList<>() : model.getShownSeatsList().getValue();
                            shownSeatsList.add(tag);
                            model.getShownSeatsList().setValue(shownSeatsList);
                            if (shownSeatsList.size() == row * column - notUseSeatTags.size()) {
                                isChangingTrigger = true;
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); //go to isChangingStageObserver
                            }
                            break;
                    }
                }
        }
    }

    private void makeGrid() {
        row = rowSeekBar.getProgress();
        column = columnSeekBar.getProgress();
        boolean isSeatNumberShow = preference.getBoolean(getString(R.string.use_seatNumber_key), false);
        int seatWidth = preference.getInt(getString(R.string.seat_width_key), SeatAppearanceDialog.DEFAULT_SEAT_WIDTH);
        int seatHeight = preference.getInt(getString(R.string.seat_height_key), SeatAppearanceDialog.DEFAULT_SEAT_HEIGHT);
        int textSize = preference.getInt(getString(R.string.seat_text_size_key), SeatAppearanceDialog.DEFAULT_SEAT_TEXT_SIZE);
        float scale = getResources().getDisplayMetrics().density;
        int _16dp = (int) (16 * scale + 0.5f);
        int _8dp = (int) (8 * scale + 0.5f);
        if (isSeatNumberShow) homeFragmentContainer.setPadding(_8dp, _8dp, _16dp, _16dp);
        else homeFragmentContainer.setPadding(_16dp, _16dp, _16dp, _16dp);
        seatsGridLayout.removeAllViews();
        verticalNumberGridLayout.removeAllViews();
        horizontalNumberGridLayout.removeAllViews();
        seatsGridLayout.setRowCount(row);
        seatsGridLayout.setColumnCount(column);
        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= column; j++) {
                TextView textView = (TextView) inflater.inflate(R.layout.grid_item, seatsGridLayout, false);
                textView.setTag(i + ":" + j);
                textView.setOnClickListener(this);
                seatsGridLayout.addView(textView);

                textView.setMinimumWidth(SeatAppearanceDialog.Companion.dpToPx(seatWidth));
                textView.setMinWidth(seatWidth);
                textView.setMaxWidth(seatWidth);
                textView.setMinimumHeight(SeatAppearanceDialog.Companion.dpToPx(seatHeight));
                textView.setMinHeight(seatHeight);
                textView.setMaxHeight(seatHeight);
                textView.setTextSize(textSize);
            }
            if (isSeatNumberShow) {
                //행 번호
                TextView textView = (TextView) inflater.inflate(R.layout.vertical_seat_number_item, seatsGridLayout, false);
                if (rowSeatNumberType.equals(getString(R.string.alphabet)))
                    textView.setText(String.valueOf(Character.toChars('A' + i - 1)));
                else textView.setText(String.valueOf(i));
                verticalNumberGridLayout.addView(textView);
            }
        }
        if (isSeatNumberShow) {
            //열 번호
            for (int i = 1; i <= column; i++) {
                TextView textView = (TextView) inflater.inflate(R.layout.horizontal_seat_number_item, seatsGridLayout, false);
                if (columnSeatNumberType.equals(getString(R.string.alphabet)))
                    textView.setText(String.valueOf(Character.toChars('A' + i - 1)));
                else textView.setText(String.valueOf(i));
                horizontalNumberGridLayout.addView(textView);
            }
            new WaitViewsToBeShown().start(); //WaitViewsToBeShown will set Padding
        }

        String seatsNumberText = getString(R.string.number_of_seats) + row * column;
        seatsNumberTV.setText(seatsNumberText);
    }

    private void makeRandomList() {
        Random random = new Random();
        ArrayList<Integer> randomNumbers = new ArrayList<>();
        int max = row * column - notUseSeatTags.size(), randomNumber;
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= column; j++) {
                String tag = i + ":" + j;
                if (notUseSeatTags.contains(tag))
                    randomNumbers.add(-1);
                else if (fixedSeatsMap.containsKey(tag)) {
                    randomNumbers.add(Integer.valueOf(Objects.requireNonNull(fixedSeatsMap.get(tag))));
                } else {
                    while (true) {
                        randomNumber = random.nextInt(max) + 1;
                        if (!randomNumbers.contains(randomNumber) //중복 X
                                && !fixedSeatsMap.containsValue(String.valueOf(randomNumber))) { //고정된 숫자 X
                            randomNumbers.add(randomNumber);
                            break;
                        }
                    }
                }
            }
        }
        model.getRandomNumbers().setValue(randomNumbers);
    }

    private void showAll() {
        ArrayList<Integer> randomNumberList = model.getRandomNumbers().getValue();
        if (randomNumberList != null) {
            int cnt = 0;
            for (int i = 1; i <= row; i++) {
                for (int j = 1; j <= column; j++) {
                    TextView child = seatsGridLayout.findViewWithTag(i + ":" + j);
                    int number = randomNumberList.get(cnt);
                    if (number != -1) child.setText(String.valueOf(number));
                    cnt++;
                }
            }
        }
    }

    private void showSeat(String tag) {
        ArrayList<Integer> randomNumberList = model.getRandomNumbers().getValue();
        if (randomNumberList != null) {
            String[] splitString = tag.split(":");
            int row = Integer.valueOf(splitString[0]), column = Integer.valueOf(splitString[1]);
            int index = (row - 1) * this.column + column - 1;
            if (randomNumberList.get(index) != -1) {
                ((TextView) seatsGridLayout.findViewWithTag(tag)).setText(String.valueOf(randomNumberList.get(index)));
            }
        }
    }

    private void reset() {
        if (interstitialAd.isLoaded()) interstitialAd.show(); //전면광고
        int resetCount = preference.getInt(getString(R.string.reset_count_key), 0);
        resetCount++;
        preferenceEditor.putInt(getString(R.string.reset_count_key), resetCount);
        preferenceEditor.apply();

        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= column; j++) {
                TextView child = seatsGridLayout.findViewWithTag(i + ":" + j);
                child.setText("");
            }
        }
        model.getShownSeatsList().setValue(new ArrayList<>());
        model.getFixedSeatsMap().setValue(new HashMap<>());
    }

    private void updateState() {
        if (model.getCurrentStage().getValue() != null) {
            switch (model.getCurrentStage().getValue()) {
                case 1:
                    //자리 설정 단계
                    updateNotUseSeat();
                    seatsNumberTV.setVisibility(View.VISIBLE);
                    indicatorStayLayout.setVisibility(View.VISIBLE);
                    cautionTV.setText(R.string.caution);
                    cautionTV.setTextColor(requireActivity().getColor(android.R.color.darker_gray));
                    cautionTV.setTypeface(null, Typeface.NORMAL);
                    cautionTV.setVisibility(View.VISIBLE);
                    stageButton.setText(getString(R.string.next));
                    stageImageView.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_baseline_check_24px));
                    break;
                case 2:
                    //자리 확인 단계
                    ArrayList<String> shownSeatsList = model.getShownSeatsList().getValue() == null ? new ArrayList<>() : model.getShownSeatsList().getValue();
                    for (String tag : shownSeatsList) showSeat(tag);
                    seatsNumberTV.setVisibility(View.GONE);
                    indicatorStayLayout.setVisibility(View.GONE);
                    cautionTV.setText(R.string.show_all_helper);
                    cautionTV.setTextColor(requireActivity().getColor(R.color.colorPrimary));
                    cautionTV.setTypeface(null, Typeface.BOLD);
                    cautionTV.setVisibility(View.VISIBLE);
                    stageButton.setText(getString(R.string.show_all));
                    break;
                case 3:
                    //초기화 단계
                    showAll();
                    seatsNumberTV.setVisibility(View.GONE);
                    indicatorStayLayout.setVisibility(View.GONE);
                    cautionTV.setVisibility(View.GONE);
                    stageButton.setText(getString(R.string.reset));
                    stageImageView.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_baseline_replay_24px));
                    break;
            }
        }
    }

    private void updateNotUseSeat() {
        if (model.getCurrentStage().getValue() != null && model.getCurrentStage().getValue() == 1) {
            HashMap<String, String> temp = new HashMap<>(fixedSeatsMap); //오류 방지를 위해 임시 배열에서 작업
            for (String tag : fixedSeatsMap.keySet()) { //고정된 자리가 자리 갯수의 범위를 벗어나거나 사용하지 않는 자리로 지정된 경우 삭제
                int numberOfSeats = row * column - notUseSeatTags.size();
                if (Integer.valueOf(Objects.requireNonNull(fixedSeatsMap.get(tag))) > numberOfSeats
                        || notUseSeatTags.contains(tag))
                    temp.remove(tag);
            }
            fixedSeatsMap = temp;
            model.getFixedSeatsMap().setValue(fixedSeatsMap);

            if (notUseSeatTags != null) {
                for (int i = 1; i <= seatsGridLayout.getRowCount(); i++) {
                    for (int j = 1; j <= seatsGridLayout.getColumnCount(); j++) {
                        TextView tv = seatsGridLayout.findViewWithTag(i + ":" + j);
                        if (notUseSeatTags.contains(tv.getTag().toString()))
                            tv.setBackgroundColor(Color.TRANSPARENT);
                        else tv.setBackgroundResource(R.drawable.rounded_square);
                    }
                }
                String seatsNumberText = getString(R.string.number_of_seats) + (row * column - notUseSeatTags.size());
                seatsNumberTV.setText(seatsNumberText);
            }
        }
    }

    private void updateFixedSeats() {
        if (model.getCurrentStage().getValue() != null && model.getCurrentStage().getValue() == 1) {
            for (int i = 1; i <= row; i++) {
                for (int j = 1; j <= column; j++) {
                    ((TextView) seatsGridLayout.findViewWithTag(i + ":" + j)).setText(fixedSeatsMap.get(i + ":" + j));
                }
            }
        }
    }

    private void initFIxNumberSpinner(Spinner spinner, String clickedSeatTag, int spinnerItem) {
        ArrayList<String> fixableValueItems = new ArrayList<>();
        fixableValueItems.add(getString(R.string.none));
        for (int i = 1; i <= row * column - notUseSeatTags.size(); i++) {
            if (!fixedSeatsMap.containsValue(String.valueOf(i)) //값이 이미 고정된 값이 아니거나
                    || String.valueOf(i).equals(fixedSeatsMap.get(clickedSeatTag))) //선택한 자리에 고정된 값이면
                fixableValueItems.add(String.valueOf(i));
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireActivity(), spinnerItem, fixableValueItems);
        spinner.setAdapter(spinnerAdapter);

        if (fixedSeatsMap.containsKey(clickedSeatTag) && fixableValueItems.contains(fixedSeatsMap.get(clickedSeatTag)))
            spinner.setSelection(fixableValueItems.indexOf(fixedSeatsMap.get(clickedSeatTag)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = ((TextView) view).getText().toString();
                if (selectedValue.equals(getString(R.string.none)))
                    fixedSeatsMap.remove(clickedSeatTag);
                else fixedSeatsMap.put(clickedSeatTag, selectedValue);
                model.getFixedSeatsMap().setValue(fixedSeatsMap);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setGridLayoutMargin() {
        int marginBottom = parent.getHeight() - bottomSheet.getTop();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) seatsGridScrollView.getLayoutParams();
        params.setMargins(0, 0, 0, marginBottom);
        seatsGridScrollView.requestLayout();
    }

    private class WaitViewsToBeShown extends Thread {
        @Override
        public void run() {
            while (parent.getHeight() == 0 || bottomSheet.getTop() == 0)
                Log.d("RandomSeatArrangement", "Waiting views to be shown");
            Log.d("RandomSeatArrangement", "Done");
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                setGridLayoutMargin();
                horizontalNumberScrollView.setPadding(verticalNumberGridLayout.getWidth(), 0, 0, 0);
            });
        }
    }
}
