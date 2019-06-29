package com.seunghyun.randomseats.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.database.HistoryDBContract
import com.seunghyun.randomseats.database.HistoryDBHelper
import com.seunghyun.randomseats.database.HistoryItem
import com.seunghyun.randomseats.recyclerview.CardItem
import com.seunghyun.randomseats.recyclerview.RecyclerAdapter
import java.util.*

class HistoryFragment : Fragment() {
    private lateinit var dbHelper: HistoryDBHelper
    private val historyItems = ArrayList<HistoryItem>()
    private lateinit var adapter: RecyclerAdapter

    private val historyAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { //From HistoryDBHelper.kt
            val historyItem = loadLastData()
            adapter.addItem(CardItem(historyItem.title, historyItem.description, historyItem.date))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(historyAddedReceiver, IntentFilter("History added"))
        initDB()
        loadAllData()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(historyAddedReceiver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val items = ArrayList<CardItem>().apply {
            historyItems.forEach {
                add(CardItem(it.title, it.description, it.date))
            }
        }

        setUpRecyclerView(view, items)
        return view
    }

    private fun setUpRecyclerView(view: View, items: ArrayList<CardItem>) {
        adapter = RecyclerAdapter(requireActivity(), items)
        with(view.findViewById<RecyclerView>(R.id.recycler_view)) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            this.adapter = adapter
        }
    }

    private fun initDB() {
        dbHelper = HistoryDBHelper(requireContext())
    }

    private fun loadAllData() {
        val cursor = dbHelper.readableDatabase.rawQuery(HistoryDBContract.SQL_SELECT, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val id = cursor.getInt(0)
            val title = cursor.getString(1)
            val description = cursor.getString(2)
            val date = cursor.getString(3)
            val seatInfo = cursor.getString(4)
            historyItems.add(HistoryItem(id, title, description, date, seatInfo))
            cursor.moveToNext()
        }
        cursor.close()
    }

    private fun loadLastData(): HistoryItem {
        val cursor = dbHelper.readableDatabase.rawQuery(HistoryDBContract.SQL_SELECT, null)
        cursor.moveToLast()
        val id = cursor.getInt(0)
        val title = cursor.getString(1)
        val description = cursor.getString(2)
        val date = cursor.getString(3)
        val seatInfo = cursor.getString(4)

        val item = HistoryItem(id, title, description, date, seatInfo)
        historyItems.add(item)
        cursor.close()
        return item
    }
}
