package com.seunghyun.randomseatarrangement

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.view.Window
import android.widget.SeekBar
import kotlinx.android.synthetic.main.dialog_seat_appearance.*

class SeatAppearanceDialog(context: Context, sharedPreferences: SharedPreferences) : Dialog(context) {
    companion object {
        const val DEFAULT_SEAT_WIDTH = 40
        const val DEFAULT_SEAT_HEIGHT = 40
        const val DEFAULT_SEAT_TEXT_SIZE = 16

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_seat_appearance)
        val editor = sharedPreferences.edit()
        okButton.setOnClickListener {
            dismiss()
        }

        with(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (seekBar) {
                    widthSeekBar -> {
                        val width = progress * 10 + 20
                        with(seatSizeItem) {
                            minimumWidth = dpToPx(width)
                            minWidth = width
                            maxWidth = width
                        }
                        editor.putInt(context.getString(R.string.seat_width_key), width)
                    }

                    heightSeekBar -> {
                        val height = (progress + 1) * 4 + 20
                        with(seatSizeItem) {
                            minimumHeight = dpToPx(height)
                            minHeight = height
                            maxHeight = height
                        }
                        editor.putInt(context.getString(R.string.seat_height_key), height)
                    }

                    textSizeSeekBar -> {
                        val size = (progress - 1) * 6 + 16
                        seatSizeItem.textSize = size.toFloat()
                        editor.putInt(context.getString(R.string.seat_text_size_key), size)
                    }
                }
                editor.apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }) {
            widthSeekBar.setOnSeekBarChangeListener(this)
            heightSeekBar.setOnSeekBarChangeListener(this)
            textSizeSeekBar.setOnSeekBarChangeListener(this)
        }

        widthSeekBar.progress = (sharedPreferences.getInt(context.getString(R.string.seat_width_key), DEFAULT_SEAT_WIDTH) - 20) / 10
        heightSeekBar.progress = (sharedPreferences.getInt(context.getString(R.string.seat_height_key), DEFAULT_SEAT_HEIGHT) - 20) / 4 - 1
        textSizeSeekBar.progress = (sharedPreferences.getInt(context.getString(R.string.seat_text_size_key), DEFAULT_SEAT_TEXT_SIZE) - 16) / 6 + 1
    }
}
