package com.mamp.qrscanner.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class PermissionActivity extends Activity {
    private static final int CAMERA_PERMISSION = 0;
    // 권한 요청 팝엄이 닫기면 호출되는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION :
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "허용 거부", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    finish();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 고객이 permission 허가를 한번이라X도 했으면 true를 return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 카메라 권한이 runtime때 획득되지 않은 상태

            // 권한 요청 팝업
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 핸드폰 버전이 마시멜로 이상이면 실행
                requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            }
        }else {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
