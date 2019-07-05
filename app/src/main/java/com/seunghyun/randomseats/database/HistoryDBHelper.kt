package com.seunghyun.randomseats.database

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.seunghyun.randomseats.database.HistoryDBContract.Companion.DB_FILE_NAME
import com.seunghyun.randomseats.database.HistoryDBContract.Companion.DB_VERSION
import java.io.ByteArrayOutputStream

class HistoryDBHelper(context: Context) : SQLiteOpenHelper(context, DB_FILE_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(HistoryDBContract.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        fun saveValues(context: Context, item: HistoryItem) {
            val byteArray = getByteArrayFromBitmap(item.seatImage)
            with(HistoryDBHelper(context).writableDatabase.compileStatement(HistoryDBContract.SQL_INSERT +
                    "(\"${item.title}\", \"${item.description}\", \"${item.date}\", \"${item.seatInfo}\", ?)")) {
                bindBlob(1, byteArray)
                execute()
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("History added")) //To HistoryFragment.kt
        }

        fun deleteValue(context: Context, item: HistoryItem) {
            HistoryDBHelper(context).writableDatabase.delete(HistoryDBContract.TABLE_NAME, HistoryDBContract.COL_ID + "=" + item.id, null)
        }

        fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
            var width = image.width
            var height = image.height

            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }
    }
}

