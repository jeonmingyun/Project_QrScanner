package com.mamp.qrscanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.mamp.qrscanner.Constacts;
import com.mamp.qrscanner.R;
import com.mamp.qrscanner.db.DbOpenHelper;
import com.mamp.qrscanner.setting.StatusBarSet;
import com.mamp.qrscanner.vo.QrDataVo;

public class MainActivity extends Activity implements View.OnClickListener {
    private CodeScanner codeScanner;
    private String lastQrData = "";
    private DbOpenHelper dbHelper;

    private StatusBarSet statusBar;
    private TextView lastQrView, scanCountView;
    private CodeScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannerView = findViewById(R.id.scanner_view);
        lastQrView = findViewById(R.id.last_qr_view);
        scanCountView = findViewById(R.id.scan_counter);

        findViewById(R.id.switch_camera_btn).setOnClickListener(this);
        findViewById(R.id.show_qr_data_btn).setOnClickListener(this);

        dbHelper = new DbOpenHelper(this);
        initStatusBar();
        initTodayQrCountTextView();
        initQrScanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        codeScanner.releaseResources();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.switch_camera_btn) {
            int cameraId = codeScanner.getCamera();
            cameraId = cameraId == Constacts.CAMERA_BACK ? Constacts.CAMERA_FRONT : Constacts.CAMERA_BACK;

            codeScanner.setCamera(cameraId);
        } else if (viewId == R.id.show_qr_data_btn) {
            Intent intent = new Intent(this, ShowDataActivity.class);
            startActivity(intent);
        } else {
            Log.e("MainActivity", "no click object");
        }
    }

    private void initStatusBar() {
        /*status bar setting*/
        statusBar = new StatusBarSet(getWindow());
        statusBar.layoutFullScreenTransparent();
    }

    private void initTodayQrCountTextView() {
        /*오늘 인식된 qr 코드 개수*/
        int statusBarHeight = statusBar.getStatusBarHeight(getResources());
        scanCountView.setText(countTodayQrData());
        scanCountView.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initQrScanner() {
        /*qr scanner start*/
        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(final Result result) {
                runOnUiThread(new Runnable() {
                    String nowQrData = result.getText();

                    @Override
                    public void run() {
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
                            if (!nowQrData.equals(lastQrData)) {
                                insertQrData(nowQrData);

                                lastQrView.setText("인식된 데이터 : " + nowQrData);
                                /*오늘 인식된 qr 코드 개수*/
//                                scanCountView.setText(countTodayQrData());

                                lastQrData = nowQrData;
                            } else {
                                Toast toast = Toast.makeText(MainActivity.this, "인식된 qr 코드입니다", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 50, 50);
                                toast.show();
                            }

                            codeScanner.startPreview();
                            /*다음 인식 딜레이 1초*/
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private boolean insertQrData(String qrData) {
        QrDataVo qrDataVo = new QrDataVo();
        qrDataVo.setQrData(qrData);

        return dbHelper.insertQrData(qrDataVo);
    }

    private String countTodayQrData() {
        Cursor cursor = dbHelper.getTodayQrData();
        String dataCount = cursor.getCount() + "";

        cursor.close();

        return dataCount;
    }
}