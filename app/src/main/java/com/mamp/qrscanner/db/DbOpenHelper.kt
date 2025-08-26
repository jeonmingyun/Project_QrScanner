package com.mamp.qrscanner.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.mamp.qrscanner.R
import com.mamp.qrscanner.db.DbContract.QrData
import com.mamp.qrscanner.vo.QrDataVo
import java.text.SimpleDateFormat
import java.util.Date

class DbOpenHelper(private val mContext: Context) :
    SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(QrData.SQL_CREATE_QR_DATA)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(QrData.SQL_DROP_QR_DATA)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    val allQrData: Cursor
        get() {
            db = this.readableDatabase

            return db!!.query(
                QrData.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )
        }

    fun getQrData(item: String, searchText: String?): Cursor {
        db = this.readableDatabase

        val itemDate = mContext.getString(R.string.input_date)
        val selection: String?
        val selectionArgs: Array<String?>?

        if (item == itemDate) {
            selection = QrData.COLUMN_INPUT_DATE + " LIKE ?"
            selectionArgs = arrayOf<String?>("%" + searchText + "%")
        } else {
            selection = QrData.COLUMN_QR_DATA + "= ?"
            selectionArgs = arrayOf<String?>(searchText)
        }

        return db!!.query(QrData.TABLE_NAME, null, selection, selectionArgs, null, null, null)
    }

    val lastQrData: Cursor
        get() {
            db = this.readableDatabase

            val orderBy: String = "${BaseColumns._ID} desc limit 1"

            return db!!.query(
                QrData.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderBy
            )
        }

    fun insertQrData(vo: QrDataVo): Boolean {
        db = this.writableDatabase

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val values = ContentValues()
        values.put(QrData.COLUMN_QR_DATA, vo.qrData)
        values.put(QrData.COLUMN_INPUT_DATE, dateFormat.format(Date()))

        val newRowId: Long = db!!.insert(QrData.TABLE_NAME, null, values)

        return newRowId != -1L
    }

    fun insertTest() {
        db = this.writableDatabase
        val sql = "insert into qr_data ('qr_data', 'input_date') VALUES(?,?)"
        db!!.rawQuery(sql, arrayOf<String>("4444", "2020-12-14 01:01:01"))
    }

    val todayQrData: Cursor
        get() {
            db = this.readableDatabase

            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val nowDate = dateFormat.format(Date())

            val selection = QrData.COLUMN_INPUT_DATE + " LIKE ?"
            val selectionArgs: Array<String?> =
                arrayOf<String?>(nowDate + " %")

            return db!!.query(
                QrData.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
        }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SQLite.db"
        private var db: SQLiteDatabase? = null
    }
}