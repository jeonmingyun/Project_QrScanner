package com.mamp.qrscanner.view.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mamp.qrscanner.view.base.BaseActivity
import com.mamp.qrscanner.view.main.MainActivity

class PermissionActivity : BaseActivity() {
    // 권한 요청 팝엄이 닫기면 호출되는 메소드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION -> if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "허용 거부", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 고객이 permission 허가를 한번이라X도 했으면 true를 return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 카메라 권한이 runtime때 획득되지 않은 상태

            // 권한 요청 팝업

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 핸드폰 버전이 마시멜로 이상이면 실행
                requestPermissions(arrayOf<String>(Manifest.permission.CAMERA), CAMERA_PERMISSION)
            }
        } else {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        private const val CAMERA_PERMISSION = 0
    }
}