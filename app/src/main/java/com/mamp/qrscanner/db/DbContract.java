package com.mamp.qrscanner.db;

import android.provider.BaseColumns;

public class DbContract {

    private DbContract() {}

    public static final class QrData implements BaseColumns {
        public static final String TABLE_NAME = "qr_data";
        public static final String COLUMN_QR_DATA = "qr_data";
        public static final String COLUMN_INPUT_DATE = "input_date";

        public static final String SQL_CREATE_QR_DATA =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_QR_DATA + " TEXT," +
                        COLUMN_INPUT_DATE + " TEXT)";

        public static final String SQL_DROP_QR_DATA =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}

