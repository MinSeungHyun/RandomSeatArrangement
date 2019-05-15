package com.seunghyun.randomseats;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.List;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<CardItem> items;
    private int itemLayout;

    RecyclerAdapter(Context context, List<CardItem> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GridLayout gridLayout;
        TextView titleTV, descriptionTV, dateTV;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.history_card);
            gridLayout = itemView.findViewById(R.id.history_seat_grid);
            titleTV = itemView.findViewById(R.id.title_tv);
            descriptionTV = itemView.findViewById(R.id.description_tv);
            dateTV = itemView.findViewById(R.id.date_tv);
        }
    }
}
