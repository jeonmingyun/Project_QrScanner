package com.mamp.qrscanner.activity;

import android.app.DatePickerDialog;
import android.app.Service;
import android.database.Cursor;
import android.os.Build;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mamp.qrscanner.R;
import com.mamp.qrscanner.StatusBarSet;
import com.mamp.qrscanner.db.DbContract;
import com.mamp.qrscanner.db.DbOpenHelper;
import com.mamp.qrscanner.listView.QrDataListViewAdapter;
import com.mamp.qrscanner.listView.QrDataListViewItem;
import com.mamp.qrscanner.vo.QrDataVo;

import java.util.ArrayList;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private DbOpenHelper dbHelper;
    private List<QrDataVo> qrDataList;
    private Spinner spinner;
    private EditText searchText;
    private TextView searchDateView;
    private List<QrDataListViewItem> qrDataListViewItems;
    ArrayAdapter<String> arrayAdapter;
    private QrDataListViewAdapter qrDataListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        dbHelper = new DbOpenHelper(this);

        getAllQrData();

        searchText = findViewById(R.id.search_text);
        spinner = findViewById(R.id.qr_search_spinner);
        searchDateView = findViewById(R.id.search_date_view);

        spinner.setOnItemSelectedListener(this);
        searchText.setOnClickListener(this);
        searchDateView.setOnClickListener(this);
        findViewById(R.id.scan_start_btn).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);

        StatusBarSet statusBar = new StatusBarSet(getWindow().getDecorView());
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

        /*search spinner adapter*/
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.qr_search_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


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
            case R.id.scan_start_btn :
                finish();
                break;
            case R.id.search_btn :
                String searchData = searchText.getText().toString();
                String item = spinner.getSelectedItem().toString();

                getQrData(item, searchData);

                InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow( searchText.getWindowToken(), 0);
                }
                searchText.setText("");
                break;
            case R.id.search_date_view :
//                String spinnerItem = spinner.getSelectedItem().toString();
                openDatePickerDialog();
//                if (getString(R.string.input_date).equals(spinnerItem)) {
//                    openDatePickerDialog();
//                }
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

    /*search qr data in DB*/
    private void getQrData(String item, String searchText) {
        Cursor cursor = dbHelper.getQrData(item, searchText);
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

    /*spinner click event*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    /*spinner click event*/
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
