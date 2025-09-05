package com.mamp.qrscanner.view.base

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    fun logE(msg: String) {
        val stackTrace = Thread.currentThread().stackTrace
        // [0]=VM, [1]=Thread, [2]=현재 메서드, [3]=호출한 위치
        val element = stackTrace[3]
        val logMsg = """
            ${element.className}.${element.methodName}
            $msg
        """

        Log.e(javaClass.simpleName, logMsg)
        Toast.makeText(this, "TAG:${javaClass.simpleName} - $logMsg", Toast.LENGTH_LONG).show()
    }
}