package com.seunghyun.randomseatarrangement.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seunghyun.randomseatarrangement.recyclerview.CardItem;
import com.seunghyun.randomseatarrangement.R;
import com.seunghyun.randomseatarrangement.recyclerview.RecyclerAdapter;
import com.seunghyun.randomseatarrangement.recyclerview.SwipeToDeleteCallback;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
//
//
//        ArrayList<CardItem> items = new ArrayList<>();
//        items.add(new CardItem("Title1", "Description1", "2019.05.16"));
//        items.add(new CardItem("Title2", "Description2", "2019.05.17"));
//
//        setUpRecyclerView(view, items);
        return view;
    }

    private void setUpRecyclerView(View view, ArrayList<CardItem> items) {
        RecyclerAdapter adapter = new RecyclerAdapter(requireActivity(), items);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(requireActivity(), adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }
}
