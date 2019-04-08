package com.seunghyun.randomseats;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


class SeatsGirdAdapter extends BaseAdapter {
    static int INITIALIZE = 1;
    static int SHOW_ALL = 2;
    static int SHOW = 3;
    static int CHANGE = 4;

    private int layout;
    private ArrayList<Integer> seatList, shownSeatsList, exceptedList, numberList;
    private SparseIntArray fixedSeatsMap;
    private LayoutInflater inf;
    private int type;

    SeatsGirdAdapter(Context context, int layout, ArrayList<Integer> seatList,
                     int type, ArrayList<Integer> exceptedList, ArrayList<Integer> shownSeatsList, ArrayList<Integer> numberList, SparseIntArray fixedSeatsMap) {
        this.layout = layout;
        this.seatList = seatList;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.type = type;
        this.shownSeatsList = shownSeatsList;
        this.exceptedList = exceptedList;
        this.numberList = numberList;
        this.fixedSeatsMap = fixedSeatsMap;
    }

    @Override
    public int getCount() {
        return seatList.size();
    }

    @Override
    public Object getItem(int position) {
        return seatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);
        TextView tv = convertView.findViewById(R.id.grid_text);

        if (exceptedList.contains(position)) tv.setBackgroundColor(Color.TRANSPARENT);
        switch (type) {
            case 1:
                tv.setText("");
                if (exceptedList.contains(position)) tv.setBackgroundColor(Color.TRANSPARENT);
                break;
            case 2:
                if (!exceptedList.contains(position))
                    tv.setText(String.valueOf(numberList.get(position)));
                break;
            case 3:
                if (!exceptedList.contains(position)) {
                    for (int seat : shownSeatsList)
                        if (position == seat) tv.setText(String.valueOf(numberList.get(position)));
                }
                break;
        }
        if (fixedSeatsMap.get(position) != 0)
            tv.setText(String.valueOf(fixedSeatsMap.get(position)));
        return convertView;
    }
}