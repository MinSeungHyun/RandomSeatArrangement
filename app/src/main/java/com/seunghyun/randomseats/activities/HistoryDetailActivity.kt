package com.seunghyun.randomseats.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class HistoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val byteArray = intent.getByteArrayExtra("image")
        val image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}