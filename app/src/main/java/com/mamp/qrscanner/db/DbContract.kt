package com.mamp.qrscanner.db

import android.provider.BaseColumns

object DbContract {
    object QrData : BaseColumns {
        const val TABLE_NAME = "qr_data"
        const val COLUMN_QR_DATA = "qr_data"
        const val COLUMN_INPUT_DATE = "input_date"

        // 테이블 생성 SQL 문
        const val SQL_CREATE_QR_DATA =
            "CREATE TABLE $TABLE_NAME (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "$COLUMN_QR_DATA TEXT," +
                    "$COLUMN_INPUT_DATE TEXT)"

        // 테이블 삭제 SQL 문
        const val SQL_DROP_QR_DATA = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
