package com.seunghyun.randomseats.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.Toast
import com.seunghyun.randomseats.R
import com.seunghyun.randomseats.database.HistoryDBHelper
import com.seunghyun.randomseats.database.HistoryItem
import kotlinx.android.synthetic.main.dialog_save_history.*
import java.text.SimpleDateFormat
import java.util.*

private const val TITLE_MAX_LENGTH = 50
private const val DESCRIPTION_MAX_LENGTH = 100

class SaveHistoryDialog(context: Context, seatInfo: String, seatBitmap: Bitmap) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_save_history)

        cancelButton.setOnClickListener {
            dismiss()
        }
        okButton.setOnClickListener {
            if (titleET.text!!.length < 2) {
                Toast.makeText(context, context.getString(R.string.text_short_error), Toast.LENGTH_LONG).show()
            } else if (titleET.text!!.length > TITLE_MAX_LENGTH || descriptionET.text!!.length > DESCRIPTION_MAX_LENGTH) {
                Toast.makeText(context, context.getString(R.string.text_over_error), Toast.LENGTH_LONG).show()
            } else {
                val item = HistoryItem(null, titleET.text.toString(), descriptionET.text.toString(), getCurrentTime(), seatInfo, seatBitmap)
                HistoryDBHelper.saveValues(context, item)
                dismiss()
            }
        }

        titleETLayout.counterMaxLength = TITLE_MAX_LENGTH
        descriptionETLayout.counterMaxLength = DESCRIPTION_MAX_LENGTH

        titleET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > TITLE_MAX_LENGTH) titleETLayout.error = context.getString(R.string.text_over_error)
                else titleETLayout.error = ""
            }
        })
        descriptionET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > DESCRIPTION_MAX_LENGTH) descriptionETLayout.error = context.getString(R.string.text_over_error)
                else descriptionETLayout.error = ""
            }
        })
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getCurrentTime(): String {
            val timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val date = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            dateFormat.timeZone = timeZone
            return dateFormat.format(date)
        }
    }
}
