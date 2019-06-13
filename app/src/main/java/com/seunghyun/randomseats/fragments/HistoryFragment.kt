package com.seunghyun.randomseats.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.recyclerview.CardItem
import com.seunghyun.randomseats.recyclerview.RecyclerAdapter
import java.util.*

class HistoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
