package com.seunghyun.randomseats.database

class HistoryDBContract {
    companion object {
        const val DB_VERSION = 1
        const val DB_FILE_NAME = "history.db"

        const val TABLE_NAME = "HISTORY"
        const val COL_ID = "ID"
        private const val COL_TITLE = "TITLE"
        private const val COL_DESCRIPTION = "DESCRIPTION"
        private const val COL_DATE = "DATE"
        private const val COL_SEAT_INFO = "SEAT_INFO"
        private const val COL_SEAT_BITMAP = "SEAT_BITMAP"

        // CREATE TABLE IF NOT EXISTS
        const val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_TITLE TEXT, " +
                "$COL_DESCRIPTION TEXT, " +
                "$COL_DATE TEXT, " +
                "$COL_SEAT_INFO TEXT, " +
                "$COL_SEAT_BITMAP BLOB" +
                ")"

        const val SQL_SELECT = "SELECT * FROM $TABLE_NAME"
        const val SQL_INSERT = "INSERT OR REPLACE INTO $TABLE_NAME ($COL_TITLE, $COL_DESCRIPTION, $COL_DATE, $COL_SEAT_INFO, $COL_SEAT_BITMAP) VALUES "
        const val SQL_DELETE = "DELETE FROM $TABLE_NAME"
    }
}