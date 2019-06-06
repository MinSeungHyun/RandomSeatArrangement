package com.seunghyun.randomseatarrangement.recyclerview;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seunghyun.randomseatarrangement.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final FragmentActivity context;
    private List<CardItem> items;
    private CardItem recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    public RecyclerAdapter(FragmentActivity context, List<CardItem> items) {
        this.items = items;
        this.context = context;
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
        viewHolder.titleTV.setText(item.getTitleText());
        viewHolder.descriptionTV.setText(item.getDescriptionText());
        viewHolder.dateTV.setText(item.getDateText());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void deleteItem(int position) {
        recentlyDeletedItem = items.get(position);
        recentlyDeletedItemPosition = position;
        items.remove(position);
        notifyItemRemoved(position);
        showUndoSnackBar();
    }

    private void showUndoSnackBar() {
        View view = context.findViewById(R.id.activity_main_container);
        Snackbar snackbar = Snackbar.make(view, R.string.item_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        items.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTV, descriptionTV, dateTV;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.history_card);
            titleTV = itemView.findViewById(R.id.title_tv);
            descriptionTV = itemView.findViewById(R.id.description_tv);
            dateTV = itemView.findViewById(R.id.date_tv);
        }
    }
}
