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
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.database.HistoryDBContract
import com.seunghyun.randomseats.database.HistoryDBHelper
import com.seunghyun.randomseats.database.HistoryItem
import com.seunghyun.randomseats.utils.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {
    private lateinit var dbHelper: HistoryDBHelper
    private val historyItems = ArrayList<HistoryItem>()
    private lateinit var mAdapter: RecyclerAdapter
    private lateinit var parent: View

    private val historyAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { //From HistoryDBHelper.kt
            val historyItem = loadLastData()
            mAdapter.addItem(historyItem)
            parent.emptyHistoryTV.visibility = View.GONE
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
        parent = inflater.inflate(R.layout.fragment_history, container, false)

        setUpRecyclerView(historyItems)
        if (historyItems.size == 0)
            parent.emptyHistoryTV.visibility = View.VISIBLE
        return parent
    }

    private fun setUpRecyclerView(items: ArrayList<HistoryItem>) {
        mAdapter = RecyclerAdapter(requireActivity(), items)
        with(parent.recycler_view) {
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
