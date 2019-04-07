package com.seunghyun.randomseats;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

class DetailSettingDialog extends Dialog implements View.OnClickListener {
    private CheckBox useSeatCheckBox;

    private int position;
    private boolean isNotUseSeatChecked;

    DetailSettingDialog(@NonNull Context context, boolean isNotUseSeatChecked, int position) {
        super(context);
        this.isNotUseSeatChecked = isNotUseSeatChecked;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_seat_settings);

        useSeatCheckBox = findViewById(R.id.use_seat_checkBox);
        TextView okButton = findViewById(R.id.ok_button);
        useSeatCheckBox.setOnClickListener(this);
        okButton.setOnClickListener(this);

        useSeatCheckBox.setChecked(isNotUseSeatChecked);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.use_seat_checkBox:
                EventBus.getDefault().post(new SendDialogDataEvent(position, useSeatCheckBox.isChecked())); //to MainActivity
                break;
            case R.id.ok_button:
                cancel();
                break;
        }
    }
}
