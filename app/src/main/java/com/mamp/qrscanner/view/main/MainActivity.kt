package com.mamp.qrscanner.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.zxing.Result
import com.mamp.qrscanner.Constacts
import com.mamp.qrscanner.R
import com.mamp.qrscanner.db.DbOpenHelper
import com.mamp.qrscanner.model.QrDataModel
import com.mamp.qrscanner.setting.StatusBarSet
import com.mamp.qrscanner.view.base.BaseActivity
import com.mamp.qrscanner.view.showData.ShowDataActivity

class MainActivity : BaseActivity(), View.OnClickListener {
    private val TAG = javaClass.simpleName
    private var codeScanner: CodeScanner? = null
    private var lastQrData: String? = ""
    private var dbHelper: DbOpenHelper? = null

    private var statusBar: StatusBarSet? = null
    private var lastQrView: TextView? = null
    private var scanCountView: TextView? = null
    private var scannerView: CodeScannerView? = null
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSystemBar(findViewById(R.id.main))

        scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        lastQrView = findViewById<TextView>(R.id.last_qr_view)
        scanCountView = findViewById<TextView>(R.id.scan_counter)

        findViewById<View?>(R.id.switch_camera_btn).setOnClickListener(this)
        findViewById<View?>(R.id.show_qr_data_btn).setOnClickListener(this)

        dbHelper = DbOpenHelper(this)
        initStatusBar()
        initTodayQrCountTextView()
        initQrScanner()

        // ViewModel observer
        observeViewModel()
    }

    private fun observeViewModel() {
        mainViewModel.cameraId.observe(this) { cameraId ->
            if(codeScanner == null)
                initQrScanner() // codeScanner 초기화

            codeScanner?.camera = cameraId
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner!!.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner!!.releaseResources()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper!!.close()
    }

    override fun onClick(view: View) {
        val viewId = view.getId()
        if (viewId == R.id.switch_camera_btn) {
            if(codeScanner != null) {
                mainViewModel.onSwitchCameraBtnClicked()
            } else
                logE("code scanner is null")
        } else if (viewId == R.id.show_qr_data_btn) {
            openShowDataActivity()
        } else {
            logE("no click object")
        }
    }

    private fun openShowDataActivity() {
        val intent = Intent(this, ShowDataActivity::class.java)
        startActivity(intent)
    }

    // Android 하단 system bar 인식
    private fun initSystemBar(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initStatusBar() {
        /*status bar setting*/
        statusBar = StatusBarSet(getWindow())
        statusBar!!.layoutFullScreenTransparent()
    }

    private fun initTodayQrCountTextView() {
        /*오늘 인식된 qr 코드 개수*/
        val statusBarHeight = statusBar!!.getStatusBarHeight(getResources())
        scanCountView!!.setText(countTodayQrData())
        scanCountView!!.setPadding(0, statusBarHeight, 0, 0)
    }

    private fun initQrScanner() {
        /*qr scanner start*/
        codeScanner = CodeScanner(this, scannerView!!)

        codeScanner!!.setDecodeCallback(object : DecodeCallback {
            override fun onDecoded(result: Result) {
                runOnUiThread(object : Runnable {
                    var nowQrData: String = result.getText()

                    override fun run() {
                        try {
                            /* QR코드 연속 인식 */
//                        if(insertQrData(nowQrData)) {
//                            lastQrView.setText(nowQrData);
//                            lastQrData = nowQrData;
//
//                            Toast toast = Toast.makeText(MainActivity.this,  nowQrData + "가 인식되었습니다", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.CENTER,50,50);
//                            toast.show();
//
//                            scanCountView.setText(countTodayQrDataStr);
//                        }

                            /*QR코드 연속 인식 제거*/

                            if (nowQrData != lastQrData) {
                                insertQrData(nowQrData)

                                lastQrView!!.setText("인식된 데이터 : " + nowQrData)

                                /*오늘 인식된 qr 코드 개수*/
//                                scanCountView.setText(countTodayQrData());
                                lastQrData = nowQrData
                            } else {
                                val toast = Toast.makeText(
                                    this@MainActivity,
                                    "인식된 qr 코드입니다",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.CENTER, 50, 50)
                                toast.show()
                            }

                            codeScanner!!.startPreview()
                            /*다음 인식 딜레이 1초*/
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                })
            }
        })
    }

    private fun insertQrData(qrData: String?): Boolean {
        val qrDataModel = QrDataModel()
        qrDataModel.qrData = qrData

        return dbHelper!!.insertQrData(qrDataModel)
    }

    private fun countTodayQrData(): String {
        val cursor = dbHelper!!.todayQrData
        val dataCount = cursor.getCount().toString() + ""

        cursor.close()

        return dataCount
    }
}