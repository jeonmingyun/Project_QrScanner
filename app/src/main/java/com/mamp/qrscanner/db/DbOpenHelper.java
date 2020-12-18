package com.mamp.qrscanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mamp.qrscanner.R;
import com.mamp.qrscanner.db.DbContract.QrData;
import com.mamp.qrscanner.vo.QrDataVo;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SQLite.db";
    private static SQLiteDatabase db;
    private Context mContext;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QrData.SQL_CREATE_QR_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QrData.SQL_DROP_QR_DATA);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor getAllQrData() {
        db = this.getReadableDatabase();

        return db.query(QrData.TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getQrData(String item, String searchText) {
        db = this.getReadableDatabase();

        String itemDate = mContext.getString(R.string.input_date);
        String selection;
        String[] selectionArgs;

        if( item.equals(itemDate)) {
            selection = QrData.COLUMN_INPUT_DATE + " LIKE ?";
            selectionArgs = new String[] {"%" + searchText + "%"};
        } else {
            selection = QrData.COLUMN_QR_DATA + "= ?";
            selectionArgs = new String[] {searchText};
        }

        return db.query(QrData.TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    public Cursor getLastQrData() {
        db = this.getReadableDatabase();

        String orderBy = QrData._ID + " desc limit 1";

        return db.query(QrData.TABLE_NAME, null, null, null, null, null, orderBy);
    }

    public boolean insertQrData(QrDataVo vo) {
        db = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(QrData.COLUMN_QR_DATA, vo.getQrData());
        values.put(QrData.COLUMN_INPUT_DATE, dateFormat.format(new Date()));

        long newRowId = db.insert(QrData.TABLE_NAME, null, values);

        if(newRowId == -1)
            return false;
        else
            return true;
    }
    public void insertTest() {
        db = this.getWritableDatabase();
        String sql = "insert into qr_data ('qr_data', 'input_date') VALUES(?,?)";
        db.rawQuery(sql, new String[] {"4444", "2020-12-14 01:01:01"});
    }

    public Cursor getTodayQrData() {
        db = this.getReadableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = dateFormat.format(new Date());

        String selection = QrData.COLUMN_INPUT_DATE + " LIKE ?";
        String[] selectionArgs = new String[] {nowDate + " %"};

        return db.query(QrData.TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

}