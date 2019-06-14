package com.seunghyun.randomseats.database

class HistoryDBContract {
    companion object {
        const val DB_VERSION = 1
        const val DB_FILE_NAME = "history.db"

        private const val TABLE_NAME = "HISTORY"
        private const val COL_ID = "ID"
        private const val COL_TITLE = "TITLE"
        private const val COL_DESCRIPTION = "DESCRIPTION"
        private const val COL_DATE = "DATE"
        private const val COL_SEAT = "SEAT"

        // CREATE TABLE IF NOT EXISTS
        const val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_TITLE TEXT, " +
                "$COL_DESCRIPTION TEXT, " +
                "$COL_DATE TEXT, " +
                "$COL_SEAT TEXT" +
                ")"

        const val SQL_SELECT = "SELECT * FROM $TABLE_NAME"
        const val SQL_INSERT = "INSERT OR REPLACE INTO $TABLE_NAME ($COL_TITLE, $COL_DESCRIPTION, $COL_DATE, $COL_SEAT) VALUES "
        const val SQL_DELETE = "DELETE FROM $TABLE_NAME"
    }
}