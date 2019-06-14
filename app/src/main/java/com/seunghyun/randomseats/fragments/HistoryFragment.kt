package com.seunghyun.randomseats.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDB()
        loadAllData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val items = ArrayList<CardItem>().apply {
            add(CardItem("Title1", "Description1", "2019.05.16"))
            add(CardItem("Title2", "Description2", "2019.05.17"))
        }

        setUpRecyclerView(view, items)
        return view
    }

    private fun setUpRecyclerView(view: View, items: ArrayList<CardItem>) {
        val adapter = RecyclerAdapter(requireActivity(), items)
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
}
