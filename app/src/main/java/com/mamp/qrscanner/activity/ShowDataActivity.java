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
import java.util.Calendar;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity implements View.OnClickListener {
    private DbOpenHelper dbHelper;
    private List<QrDataVo> qrDataList;
    private QrDataListViewAdapter qrDataListAdapter;

    private TextView searchDateView;
    private Toolbar toolbar;
    private StatusBarSet statusBar;
    private ListView qrDataListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        searchDateView = findViewById(R.id.search_date_view);
        toolbar = findViewById(R.id.show_data_toolbar);
        qrDataListView = findViewById(R.id.qr_data_list);

        searchDateView.setOnClickListener(this);

        dbHelper = new DbOpenHelper(this);
        getAllQrData();
        initStatusBar();
        initActionBar();
        initQrDataList(getQrDataItems(qrDataList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void initStatusBar() {
        statusBar = new StatusBarSet(getWindow());
        statusBar.statusBarTransparent();
    }

    private void initActionBar() {
        /*create action bar*/
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.baseObject));
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

    private void initQrDataList(List<QrDataListViewItem> itemList) {
        qrDataListAdapter = new QrDataListViewAdapter(this, itemList);
        qrDataListView.setAdapter(qrDataListAdapter);
    }

    private List<QrDataListViewItem> getQrDataItems(List<QrDataVo> qrDataList) {
        List<QrDataListViewItem> qrDataListViewItems = new ArrayList<>();
        QrDataListViewItem qrDataItem;

        for( int i = 0; i < qrDataList.size(); i++) {
            qrDataItem = new QrDataListViewItem();
            qrDataItem.setQrDataIcon(ContextCompat.getDrawable(this, R.drawable.qr_data_icon));
            qrDataItem.setQrData(qrDataList.get(i).getQrData());
            qrDataItem.setQrDataDate(qrDataList.get(i).getInputDate());

            qrDataListViewItems.add(qrDataItem);
        }
        return qrDataListViewItems;
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

    public void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE) ;

        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, date);
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

            String dateStrType1 = year + "년 " + formattedMonth + "월 " + formattedDayOfMonth + "일";
            String dateStrType2 = year + ". " + formattedMonth + ". " + formattedDayOfMonth;
            searchDateView.setText(dateStrType1);
            qrDataListAdapter.getFilter().filter(dateStrType2);
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
            String formatDate = formatDate(cursor.getString(cursor.getColumnIndex(DbContract.QrData.COLUMN_INPUT_DATE)));
            qrDataVo.setInputDate(formatDate);

            qrDataList.add(qrDataVo);
        }

        cursor.close();
    }

    /** @date : yyyy-MM-dd HH:mm:ss*/
    public String formatDate(String time) {
        String[] timeArr = time.split("-|\\s|:");
        String year = timeArr[0];
        String month = timeArr[1];
        String date = timeArr[2];
        String hour = timeArr[3];
        String minute = timeArr[4];
        String second = timeArr[5];

        String formatDate = year + ". " + month + ". " + date + ". ";
        String formatTime = ""; // HH:mm:ss
        int hourInt = Integer.parseInt(hour);

        if(hourInt >= 12) {
            formatTime = "오후 " + (hourInt-12) + ":" + minute + ":" + second;
        } else {
            formatTime = "오전 " + hourInt + ":" + minute + ":" + second;
        }

        return formatDate + formatTime;
    }

}
