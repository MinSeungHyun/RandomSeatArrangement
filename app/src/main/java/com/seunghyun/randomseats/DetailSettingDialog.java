package com.seunghyun.randomseats;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

class DetailSettingDialog extends Dialog implements View.OnClickListener {
    private ArrayList<Integer> seatList;
    private SparseIntArray fixedSeatsMap;
    private CheckBox useSeatCheckBox, fixSeatCheckBox;
    private Spinner fixSeatSpinner;
    private Context context;
    private int position;
    private boolean isNotUseSeatChecked;

    DetailSettingDialog(@NonNull Context context, boolean isNotUseSeatChecked, int position, ArrayList<Integer> seatList, SparseIntArray fixedSeatsMap) {
        super(context);
        this.context = context;
        this.isNotUseSeatChecked = isNotUseSeatChecked;
        this.position = position;
        this.seatList = seatList;
        this.fixedSeatsMap = fixedSeatsMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_seat_settings);

        initialize();
        updateStatus();

        fixSeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new SendDialogFIxEvent(DetailSettingDialog.this.position, (int) fixSeatSpinner.getSelectedItem(), fixSeatCheckBox.isChecked())); //to MainActivity
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.use_seat_checkBox:
                EventBus.getDefault().post(new SendDialogUseEvent(position, useSeatCheckBox.isChecked())); //to MainActivity
                updateStatus();
                break;
            case R.id.fix_seat_checkBox:
                EventBus.getDefault().post(new SendDialogFIxEvent(position, (int) fixSeatSpinner.getSelectedItem(), fixSeatCheckBox.isChecked())); //to MainActivity
                updateStatus();
                break;
            case R.id.ok_button:
                cancel();
                break;
        }
    }

    private void initialize() {
        useSeatCheckBox = findViewById(R.id.use_seat_checkBox);
        fixSeatCheckBox = findViewById(R.id.fix_seat_checkBox);
        TextView okButton = findViewById(R.id.ok_button);
        fixSeatSpinner = findViewById(R.id.number_spinner);
        useSeatCheckBox.setOnClickListener(this);
        fixSeatCheckBox.setOnClickListener(this);
        okButton.setOnClickListener(this);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, seatList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        fixSeatSpinner.setAdapter(adapter);

        useSeatCheckBox.setChecked(isNotUseSeatChecked);
        if (fixedSeatsMap.get(position) != 0) {
            fixSeatCheckBox.setChecked(true);
            fixSeatSpinner.setSelection(adapter.getPosition(fixedSeatsMap.get(position)));
        }
    }

    private void updateStatus() {
        if (useSeatCheckBox.isChecked()) {
            fixSeatCheckBox.setEnabled(false);
            fixSeatSpinner.setEnabled(false);
        } else if (fixSeatCheckBox.isChecked()) useSeatCheckBox.setEnabled(false);
        else {
            useSeatCheckBox.setEnabled(true);
            fixSeatCheckBox.setEnabled(true);
            fixSeatSpinner.setEnabled(true);
        }
    }
}
