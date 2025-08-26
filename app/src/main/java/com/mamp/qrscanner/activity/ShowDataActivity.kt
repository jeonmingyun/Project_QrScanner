package com.mamp.qrscanner.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.ListView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import com.mamp.qrscanner.R
import com.mamp.qrscanner.db.DbContract
import com.mamp.qrscanner.db.DbOpenHelper
import com.mamp.qrscanner.listView.QrDataListViewAdapter
import com.mamp.qrscanner.listView.QrDataListViewItem
import com.mamp.qrscanner.setting.StatusBarSet
import com.mamp.qrscanner.vo.QrDataVo
import java.util.Calendar

class ShowDataActivity : Activity(), View.OnClickListener {
    private var dbHelper: DbOpenHelper? = null
    private var qrDataList: MutableList<QrDataVo?>? = null
    private var qrDataListAdapter: QrDataListViewAdapter? = null

    private var searchDateView: TextView? = null
    private var toolbar: Toolbar? = null
    private var statusBar: StatusBarSet? = null
    private var qrDataListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_data)

        searchDateView = findViewById<TextView>(R.id.search_date_view)
        toolbar = findViewById<Toolbar>(R.id.show_data_toolbar)
        qrDataListView = findViewById<ListView>(R.id.qr_data_list)

        searchDateView?.setOnClickListener(this)

        dbHelper = DbOpenHelper(this)
        this.allQrData
        initStatusBar()
        initActionBar()
        initQrDataList(getQrDataItems(qrDataList))
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper?.close()
    }

    private fun initStatusBar() {
        statusBar = StatusBarSet(getWindow())
        statusBar?.statusBarTransparent()
    }

    private fun initActionBar() {
        /*create action bar*/
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
        setActionBar(toolbar)
        val actionBar = getActionBar()
        actionBar!!.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) //기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar!!.setBackgroundColor(ContextCompat.getColor(this, R.color.baseObject))
    }

    /*toolbar*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //toolbar의 back키 눌렀을 때 동작
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initQrDataList(itemList: MutableList<QrDataListViewItem?>) {
        qrDataListAdapter = QrDataListViewAdapter(this, itemList)
        qrDataListView!!.setAdapter(qrDataListAdapter)
    }

    private fun getQrDataItems(qrDataList: MutableList<QrDataVo?>?): MutableList<QrDataListViewItem?> {
        val qrDataListViewItems: MutableList<QrDataListViewItem?> = ArrayList<QrDataListViewItem?>()
        var qrDataItem: QrDataListViewItem?

        if(qrDataList != null) {
            for (i in qrDataList.indices) {
                qrDataItem = QrDataListViewItem()
                qrDataItem.qrDataIcon = ContextCompat.getDrawable(this, R.drawable.qr_data_icon)
                qrDataItem.qrData = qrDataList.get(i)?.qrData
                qrDataItem.qrDataDate = qrDataList.get(i)?.inputDate

                qrDataListViewItems.add(qrDataItem)
            }
        }
        return qrDataListViewItems
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.search_menu, menu)

        val searchView = MenuItemCompat.getActionView(menu.findItem(R.id.search)) as SearchView
        searchView.setQueryHint("QR 데이터 검색")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                qrDataListAdapter?.getFilter()?.filter(s)

                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onClick(v: View) {
        val viewId = v.getId()

        if (viewId == R.id.search_date_view) {
            openDatePickerDialog()
        } else {
            Log.e("ShowDataActivity", "no click object")
        }
    }

    fun openDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)

        val dialog: DatePickerDialog = DatePickerDialog(this, listener, year, month, date)
        dialog.show()
    }

    private val listener: DatePickerDialog.OnDateSetListener? = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            val month = monthOfYear + 1
            var formattedMonth = "" + month
            var formattedDayOfMonth = "" + dayOfMonth

            if (month < 10) {
                formattedMonth = "0" + month
            }
            if (dayOfMonth < 10) {
                formattedDayOfMonth = "0" + dayOfMonth
            }

            val dateStrType1 =
                year.toString() + "년 " + formattedMonth + "월 " + formattedDayOfMonth + "일"
            val dateStrType2 = year.toString() + ". " + formattedMonth + ". " + formattedDayOfMonth
            searchDateView?.setText(dateStrType1)
            qrDataListAdapter?.getFilter()?.filter(dateStrType2)
        }
    }

    private val allQrData: Unit
        /*show all qr data in DB*/
        get() {
            val cursor = dbHelper?.allQrData
            qrDataList = ArrayList<QrDataVo?>()
            var qrDataVo: QrDataVo?

            while (cursor != null && cursor.moveToNext()) {
                qrDataVo = QrDataVo()
                qrDataVo.id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID) as Int)
                qrDataVo.qrData =
                    cursor.getString(cursor.getColumnIndex(DbContract.QrData.COLUMN_QR_DATA) as Int)
                val formatDate =
                    formatDate(cursor.getString(cursor.getColumnIndex(DbContract.QrData.COLUMN_INPUT_DATE) as Int))
                qrDataVo.inputDate = formatDate

                qrDataList!!.add(qrDataVo)
            }

            cursor?.close()
        }

    /** @date : yyyy-MM-dd HH:mm:ss
     */
    fun formatDate(time: String): String {
        val timeArr = time.split("-|\\s|:".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val year: String? = timeArr[0]
        val month: String? = timeArr[1]
        val date: String? = timeArr[2]
        val hour = timeArr[3]
        val minute: String? = timeArr[4]
        val second: String? = timeArr[5]

        val formatDate = year + ". " + month + ". " + date + ". "
        var formatTime = "" // HH:mm:ss
        val hourInt = hour.toInt()

        if (hourInt >= 12) {
            formatTime = "오후 " + (hourInt - 12) + ":" + minute + ":" + second
        } else {
            formatTime = "오전 " + hourInt + ":" + minute + ":" + second
        }

        return formatDate + formatTime
    }
}
