package com.mamp.qrscanner.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CodeScanner codeScanner;
    private TextView lastQrView, scanCountView;
    private String lastQrData = "";
    private DbOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbOpenHelper(this);

        findViewById(R.id.switch_camera_btn).setOnClickListener(this);
        findViewById(R.id.show_qr_data_btn).setOnClickListener(this);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        lastQrView = findViewById(R.id.last_qr_view);
        scanCountView = findViewById(R.id.scan_count_view);

        StatusBarSet statusBar = new StatusBarSet(getWindow());
        statusBar.changeIconColor();

        scanCountView.setText(countTodayQrData());

        /*qr scanner start*/
        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    String nowQrData = result.getText();
                    @Override
                    public void run() {

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
                        if(!nowQrData.equals(lastQrData)) {
                            insertQrData(nowQrData);

                            lastQrView.setText(nowQrData);
                            scanCountView.setText(countTodayQrData());

                            lastQrData = nowQrData;
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this, "인식된 qr 코드", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,50,50);
                            toast.show();
                        }

                        codeScanner.startPreview();
                    }
                });
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_camera_btn:
                int cameraId = codeScanner.getCamera();
                cameraId = cameraId == Constacts.CAMERA_BACK ? Constacts.CAMERA_FRONT : Constacts.CAMERA_BACK;

                codeScanner.setCamera(cameraId);
                break;
            case R.id.show_qr_data_btn:
                Intent intent = new Intent(this, ShowDataActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e("MainActivity", "no click object");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    private boolean insertQrData(String qrData) {
        QrDataVo qrDataVo = new QrDataVo();
        qrDataVo.setQrData(qrData);

        return dbHelper.insertQrData(qrDataVo);
    }

    private String countTodayQrData() {
        Cursor cursor = dbHelper.getTodayQrData();
        String dataCount = cursor.getCount()+"";

        cursor.close();

        return dataCount;
    }
}