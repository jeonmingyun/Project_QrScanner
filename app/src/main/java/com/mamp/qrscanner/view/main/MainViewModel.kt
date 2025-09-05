package com.mamp.qrscanner.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.budiyev.android.codescanner.CodeScanner
import com.mamp.qrscanner.Constacts
import com.mamp.qrscanner.view.base.BaseViewModel

class MainViewModel: BaseViewModel() {
    private val _count = MutableLiveData<Int>(0)
    val count: LiveData<Int> = _count

    fun onButtonClicked() {
        _count.value = _count.value!! + 1
    }

    fun onSwitchCameraBtnClicked(codeScanner: CodeScanner) {
        var cameraId = codeScanner.getCamera()
        cameraId =
            if (cameraId == Constacts.CAMERA_BACK) Constacts.CAMERA_FRONT else Constacts.CAMERA_BACK

        codeScanner.setCamera(cameraId)
    }
}