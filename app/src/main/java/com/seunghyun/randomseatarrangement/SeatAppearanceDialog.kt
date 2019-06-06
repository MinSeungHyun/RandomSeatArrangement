package com.seunghyun.randomseatarrangement

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.view.Window
import android.widget.SeekBar
import kotlinx.android.synthetic.main.dialog_seat_appearance.*

class SeatAppearanceDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_seat_appearance)
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
                    }

                    heightSeekBar -> {
                        val height = (progress + 1) * 4 + 20
                        with(seatSizeItem) {
                            minimumHeight = dpToPx(height)
                            minHeight = height
                            maxHeight = height
                        }
                    }

                    textSizeSeekBar -> {
                        val size = (progress - 1) * 6 + 16
                        seatSizeItem.textSize = size.toFloat()
                    }
                }
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
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}
