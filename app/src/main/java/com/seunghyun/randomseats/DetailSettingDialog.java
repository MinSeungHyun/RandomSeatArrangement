package com.seunghyun.randomseats;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

class DetailSettingDialog extends Dialog implements View.OnClickListener {
    private ArrayList<Integer> seatList;
    private CheckBox useSeatCheckBox, fixSeatCheckBox;
    private Spinner fixSeatSpinner;
    private Context context;
    private int position;
    private boolean isNotUseSeatChecked;

    DetailSettingDialog(@NonNull Context context, boolean isNotUseSeatChecked, int position, ArrayList<Integer> seatList) {
        super(context);
        this.context = context;
        this.isNotUseSeatChecked = isNotUseSeatChecked;
        this.position = position;
        this.seatList = seatList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_seat_settings);

        useSeatCheckBox = findViewById(R.id.use_seat_checkBox);
        fixSeatCheckBox = findViewById(R.id.fix_seat_checkBox);
        TextView okButton = findViewById(R.id.ok_button);
        useSeatCheckBox.setOnClickListener(this);
        fixSeatCheckBox.setOnClickListener(this);
        okButton.setOnClickListener(this);
        fixSeatSpinner = findViewById(R.id.number_spinner);

        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, seatList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fixSeatSpinner.setAdapter(adapter);

        useSeatCheckBox.setChecked(isNotUseSeatChecked);
        updateStatus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.use_seat_checkBox:
                EventBus.getDefault().post(new SendDialogDataEvent(position, useSeatCheckBox.isChecked())); //to MainActivity
                updateStatus();
                break;
            case R.id.fix_seat_checkBox:
                updateStatus();
                break;
            case R.id.ok_button:
                cancel();
                break;
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
