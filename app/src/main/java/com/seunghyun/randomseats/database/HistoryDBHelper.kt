package com.seunghyun.randomseats.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.seunghyun.randomseats.database.HistoryDBContract.Companion.DB_FILE_NAME
import com.seunghyun.randomseats.database.HistoryDBContract.Companion.DB_VERSION

class HistoryDBHelper(context: Context) : SQLiteOpenHelper(context, DB_FILE_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(HistoryDBContract.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        fun saveToDatabase() {

        }
    }
}

