package com.seunghyun.randomseats.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.seunghyun.randomseats.R;
import com.seunghyun.randomseats.activities.HistoryDetailActivity;
import com.seunghyun.randomseats.database.HistoryDBHelper;
import com.seunghyun.randomseats.database.HistoryItem;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final FragmentActivity context;
    private List<HistoryItem> items;
    private HistoryItem recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    public RecyclerAdapter(FragmentActivity context, List<HistoryItem> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        HistoryItem item = items.get(position);
        viewHolder.titleTV.setText(item.getTitle());
        viewHolder.descriptionTV.setText(item.getDescription());
        viewHolder.dateTV.setText(item.getDate());
        viewHolder.seatImage.setImageBitmap(item.getSeatImage());

        viewHolder.clickableView.setOnClickListener(v -> {
            Context context = v.getContext();
            HistoryItem clickedItem = items.get(position);
            Intent intent = new Intent(context, HistoryDetailActivity.class);
            intent.putExtra("title", clickedItem.getTitle());
            intent.putExtra("description", clickedItem.getDescription());
            intent.putExtra("date", clickedItem.getDate());
            intent.putExtra("image", HistoryDBHelper.Companion.getByteArrayFromBitmap(clickedItem.getSeatImage()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(HistoryItem item) {
        items.add(item);
        notifyDataSetChanged();
        Toast.makeText(context, R.string.item_added, Toast.LENGTH_LONG).show();
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
        ImageView seatImage;
        View clickableView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.history_card);
            titleTV = itemView.findViewById(R.id.title_tv);
            descriptionTV = itemView.findViewById(R.id.description_tv);
            dateTV = itemView.findViewById(R.id.date_tv);
            seatImage = itemView.findViewById(R.id.seat_image);
            clickableView = itemView.findViewById(R.id.clickable_view);
        }
    }
}
