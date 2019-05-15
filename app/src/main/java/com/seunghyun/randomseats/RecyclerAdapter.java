package com.seunghyun.randomseats;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.List;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<CardItem> items;

    RecyclerAdapter(List<CardItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CardItem item = items.get(position);
        viewHolder.gridLayout = item.getGridLayout();
        viewHolder.titleTV.setText(item.getTitleText());
        viewHolder.descriptionTV.setText(item.getDescriptionText());
        viewHolder.dateTV.setText(item.getDateText());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GridLayout gridLayout;
        TextView titleTV, descriptionTV, dateTV;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.history_card);
            gridLayout = itemView.findViewById(R.id.history_seat_grid);
            titleTV = itemView.findViewById(R.id.title_tv);
            descriptionTV = itemView.findViewById(R.id.description_tv);
            dateTV = itemView.findViewById(R.id.date_tv);
        }
    }
}
