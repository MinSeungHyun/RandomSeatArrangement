package com.seunghyun.randomseatarrangement.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.TextView
import com.seunghyun.randomseatarrangement.R
import com.seunghyun.randomseatarrangement.SeatAppearanceDialog
import com.seunghyun.randomseatarrangement.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_full_screen.*
import java.util.*

class FullScreenActivity : AppCompatActivity() {
    private var mX = 0f
    private var isNotFirstTouch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        horizontalNumberScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ -> seatsHorizontalScrollView.scrollX = scrollX }
        seatsHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ -> horizontalNumberScrollView.scrollX = scrollX }
        backButton.setOnClickListener { finish() }
        seatsScrollView.setOnTouchListener { _, event ->
            val curX: Float
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    curX = event.x
                    if (isNotFirstTouch) {
                        val x = ((mX - curX) * 1.2).toInt()
                        seatsHorizontalScrollView.scrollBy(x, 0)
                    }
                    mX = curX
                    isNotFirstTouch = true
                }
                MotionEvent.ACTION_UP -> isNotFirstTouch = false
            }
            false
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isSeatNumberShow = preferences.getBoolean(getString(R.string.use_seatNumber_key), false)
        val rowSeatNumberType = preferences.getString(getString(R.string.row_seat_number_key), getString(R.string.alphabet))!!
        val columnSeatNumberType = preferences.getString(getString(R.string.column_seat_number_key), getString(R.string.number))!!
        val seatWidth = preferences.getInt(getString(R.string.seat_width_key), SeatAppearanceDialog.DEFAULT_SEAT_WIDTH)
        val seatHeight = preferences.getInt(getString(R.string.seat_height_key), SeatAppearanceDialog.DEFAULT_SEAT_HEIGHT)
        val textSize = preferences.getInt(getString(R.string.seat_text_size_key), SeatAppearanceDialog.DEFAULT_SEAT_TEXT_SIZE)
        makeGrid(HomeFragment.row, HomeFragment.column, isSeatNumberShow, rowSeatNumberType, columnSeatNumberType, seatWidth, seatHeight, textSize)
    }

    private fun makeGrid(row: Int, column: Int, isSeatNumberShow: Boolean, rowSeatNumberType: String, columnSeatNumberType: String, seatWidth: Int, seatHeight: Int, textSize: Int) {
        seatsGridLayout.rowCount = row
        seatsGridLayout.columnCount = column
        val inflater = Objects.requireNonNull<FragmentActivity>(this).applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in 1..row) {
            for (j in 1..column) {
                val textView = inflater.inflate(R.layout.grid_item, seatsGridLayout, false) as TextView
                seatsGridLayout.addView(textView)
                with(textView) {
                    minimumWidth = SeatAppearanceDialog.dpToPx(seatWidth)
                    minWidth = seatWidth
                    maxWidth = seatWidth
                    minimumHeight = SeatAppearanceDialog.dpToPx(seatHeight)
                    minHeight = seatHeight
                    maxHeight = seatHeight
                    setTextSize(textSize.toFloat())
                }
            }
            if (isSeatNumberShow) {
                //행 번호
                val textView = inflater.inflate(R.layout.vertical_seat_number_item, seatsGridLayout, false) as TextView
                if (rowSeatNumberType == getString(R.string.alphabet))
                    textView.text = String(Character.toChars('A'.toInt() + i - 1))
                else
                    textView.text = i.toString()
                verticalNumberGridLayout.addView(textView)
            }
        }
        if (isSeatNumberShow) {
            //열 번호
            for (i in 1..column) {
                val textView = inflater.inflate(R.layout.horizontal_seat_number_item, seatsGridLayout, false) as TextView
                if (columnSeatNumberType == getString(R.string.alphabet))
                    textView.text = String(Character.toChars('A'.toInt() + i - 1))
                else
                    textView.text = i.toString()
                horizontalNumberGridLayout.addView(textView)
            }
            WaitViewsToBeShown().start() //WaitViewsToBeShown will set Padding
        }
    }

    private inner class WaitViewsToBeShown : Thread() {
        override fun run() {
            while (container.height == 0)
                Log.d("RandomSeatArrangement", "Waiting views to be shown")
            Log.d("RandomSeatArrangement", "Done")
            runOnUiThread {
                horizontalNumberScrollView.setPadding(verticalNumberGridLayout.width, 0, 0, 0)
            }
        }
    }
}
