package com.mamp.qrscanner.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamp.qrscanner.Constacts
import com.mamp.qrscanner.view.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    private val _count = MutableLiveData<Int>(0)
    val count: LiveData<Int> = _count

    // 카메라 전환
    private val _cameraId = MutableLiveData<Int>(Constacts.CAMERA_BACK)
    val cameraId: LiveData<Int> = _cameraId


    fun onButtonClicked() {
        _count.value = _count.value!! + 1
    }

    // 카메라 앞뒤 전환
    fun onSwitchCameraBtnClicked() {
        _cameraId.value =
            if (_cameraId.value == Constacts.CAMERA_BACK)
                Constacts.CAMERA_FRONT
            else
                Constacts.CAMERA_BACK
    }
}