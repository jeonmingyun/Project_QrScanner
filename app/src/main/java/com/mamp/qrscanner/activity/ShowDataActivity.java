package com.mamp.qrscanner.activity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.mamp.qrscanner.R;
import com.mamp.qrscanner.db.DbContract;
import com.mamp.qrscanner.db.DbOpenHelper;
import com.mamp.qrscanner.listView.QrDataListViewAdapter;
import com.mamp.qrscanner.listView.QrDataListViewItem;
import com.mamp.qrscanner.setting.StatusBarSet;
import com.mamp.qrscanner.vo.QrDataVo;

import java.util.ArrayList;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity implements View.OnClickListener {
    private DbOpenHelper dbHelper;
    private List<QrDataVo> qrDataList;
    private TextView searchDateView;
    private List<QrDataListViewItem> qrDataListViewItems;
    private QrDataListViewAdapter qrDataListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        dbHelper = new DbOpenHelper(this);

        getAllQrData();

        searchDateView = findViewById(R.id.search_date_view);

        searchDateView.setOnClickListener(this);

        StatusBarSet statusBar = new StatusBarSet(getWindow());
        statusBar.changeIconColor();

        /*create action bar*/
        Toolbar toolbar = findViewById(R.id.show_data_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

        /*search view list*/
        setQrDataItem();
        ListView qrDataListView = findViewById(R.id.qr_data_list);
        qrDataListAdapter = new QrDataListViewAdapter(this, qrDataListViewItems);
        qrDataListView.setAdapter(qrDataListAdapter);

    }

    private void setQrDataItem() {
        qrDataListViewItems = new ArrayList<>();
        QrDataListViewItem qrDataItem;

        for( int i = 0; i < qrDataList.size(); i++) {
            qrDataItem = new QrDataListViewItem();
            qrDataItem.setQrDataIcon(ContextCompat.getDrawable(this, R.drawable.qr_data_icon));
            qrDataItem.setQrData(qrDataList.get(i).getQrData());
            qrDataItem.setQrDataDate(qrDataList.get(i).getInputDate());

            qrDataListViewItems.add(qrDataItem);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("QR 데이터 검색");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                qrDataListAdapter.getFilter().filter(s);

                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_date_view :
                openDatePickerDialog();
                break;
            default:
                Log.e("ShowDataActivity", "no click object");
        }

    }

    private void openDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, listener, 2020, 11, 10);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int month = monthOfYear + 1;
            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if(month < 10){
                formattedMonth = "0" + month;
            }
            if(dayOfMonth < 10){
                formattedDayOfMonth = "0" + dayOfMonth;
            }

            String date = year + "-" + formattedMonth + "-" + formattedDayOfMonth;
            searchDateView.setText(date);

            qrDataListAdapter.getFilter().filter(date);
        }
    };

    /*show all qr data in DB*/
    private void getAllQrData() {
        Cursor cursor = dbHelper.getAllQrData();
        qrDataList = new ArrayList<>();
        QrDataVo qrDataVo;

        while(cursor.moveToNext()) {
            qrDataVo = new QrDataVo();
            qrDataVo.setId(cursor.getString(cursor.getColumnIndex(DbContract.QrData._ID)));
            qrDataVo.setQrData(cursor.getString(cursor.getColumnIndex(DbContract.QrData.COLUMN_QR_DATA)));
            qrDataVo.setInputDate(cursor.getString(cursor.getColumnIndex(DbContract.QrData.COLUMN_INPUT_DATE)));

            qrDataList.add(qrDataVo);
        }

        cursor.close();
    }

    /*toolbar*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {//toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
