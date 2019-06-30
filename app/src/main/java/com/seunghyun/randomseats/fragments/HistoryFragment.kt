package com.seunghyun.randomseats.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
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

class HistoryFragment : Fragment() {
    private lateinit var dbHelper: HistoryDBHelper
    private val historyItems = ArrayList<HistoryItem>()
    private lateinit var mAdapter: RecyclerAdapter

    private val historyAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { //From HistoryDBHelper.kt
            val historyItem = loadLastData()
            mAdapter.addItem(CardItem(historyItem.title, historyItem.description, historyItem.date, historyItem.seatImage))
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
                add(CardItem(it.title, it.description, it.date, it.seatImage))
            }
        }

        setUpRecyclerView(view, items)
        return view
    }

    private fun setUpRecyclerView(view: View, items: ArrayList<CardItem>) {
        mAdapter = RecyclerAdapter(requireActivity(), items)
        with(view.findViewById<RecyclerView>(R.id.recycler_view)) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = mAdapter
        }
    }

    private fun initDB() {
        dbHelper = HistoryDBHelper(requireContext())
    }

    private fun loadAllData() {
        val cursor = dbHelper.readableDatabase.rawQuery(HistoryDBContract.SQL_SELECT, null)
        cursor.moveToFirst()
        val dbItem = DBItem(cursor)
        while (!cursor.isAfterLast) {
            historyItems.add(HistoryItem(dbItem.getId(), dbItem.getTitle(), dbItem.getDescription(), dbItem.getDate(), dbItem.getSeatInfo(), dbItem.getSeatImage()))
            cursor.moveToNext()
        }
        cursor.close()
    }

    private fun loadLastData(): HistoryItem {
        val cursor = dbHelper.readableDatabase.rawQuery(HistoryDBContract.SQL_SELECT, null)
        cursor.moveToLast()
        val dbItem = DBItem(cursor)
        val item = HistoryItem(dbItem.getId(), dbItem.getTitle(), dbItem.getDescription(), dbItem.getDate(), dbItem.getSeatInfo(), dbItem.getSeatImage())
        historyItems.add(item)
        cursor.close()
        return item
    }

    private class DBItem(val cursor: Cursor) {
        fun getId() = cursor.getInt(0)
        fun getTitle() = cursor.getString(1)!!
        fun getDescription() = cursor.getString(2)!!
        fun getDate() = cursor.getString(3)!!
        fun getSeatInfo() = cursor.getString(4)!!
        fun getSeatImage() = cursor.getBlob(5).let {
            BitmapFactory.decodeByteArray(it, 0, it.size)!!
        }
    }
}
