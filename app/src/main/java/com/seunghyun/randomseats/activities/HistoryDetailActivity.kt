package com.seunghyun.randomseats.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.seunghyun.randomseats.R
import kotlinx.android.synthetic.main.activity_history_detail.*

class HistoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val byteArray = intent.getByteArrayExtra("image")
        val image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        Log.d("testing", toolbar.toString())
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        (collapsing_toolbar as CollapsingToolbarLayout).title = title
        seatImageView.setImageBitmap(image)
    }
}