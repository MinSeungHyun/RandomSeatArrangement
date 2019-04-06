package com.seunghyun.randomseats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


class SeatsGirdAdapter extends BaseAdapter {
    static int INITIALIZE = 1;
    static int SHOW_ALL = 2;

    private int layout;
    private ArrayList<Integer> item;
    private LayoutInflater inf;
    private int type;

    SeatsGirdAdapter(Context context, int layout, ArrayList<Integer> item, int type) {
        this.layout = layout;
        this.item = item;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.type = type;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
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

        switch (type){
            case 1:
                tv.setText("");
                break;
            case 2:
                Collections.shuffle(item);
                tv.setText(String.valueOf(item.get(position)));
                break;
        }
        return convertView;
    }

    public void setText() {

    }
}