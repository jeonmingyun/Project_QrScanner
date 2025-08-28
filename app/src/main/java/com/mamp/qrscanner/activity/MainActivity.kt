package com.mamp.qrscanner.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.zxing.Result
import com.mamp.qrscanner.Constacts
import com.mamp.qrscanner.R
import com.mamp.qrscanner.db.DbOpenHelper
import com.mamp.qrscanner.setting.StatusBarSet
import com.mamp.qrscanner.vo.QrDataVo

class MainActivity : Activity(), View.OnClickListener {
    private var codeScanner: CodeScanner? = null
    private var lastQrData: String? = ""
    private var dbHelper: DbOpenHelper? = null

    private var statusBar: StatusBarSet? = null
    private var lastQrView: TextView? = null
    private var scanCountView: TextView? = null
    private var scannerView: CodeScannerView? = null

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
            var cameraId = codeScanner!!.getCamera()
            cameraId =
                if (cameraId == Constacts.CAMERA_BACK) Constacts.CAMERA_FRONT else Constacts.CAMERA_BACK

            codeScanner!!.setCamera(cameraId)
        } else if (viewId == R.id.show_qr_data_btn) {
            val intent = Intent(this, ShowDataActivity::class.java)
            startActivity(intent)
        } else {
            Log.e("MainActivity", "no click object")
        }
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
        val qrDataVo = QrDataVo()
        qrDataVo.qrData = qrData

        return dbHelper!!.insertQrData(qrDataVo)
    }

    private fun countTodayQrData(): String {
        val cursor = dbHelper!!.todayQrData
        val dataCount = cursor.getCount().toString() + ""

        cursor.close()

        return dataCount
    }
}