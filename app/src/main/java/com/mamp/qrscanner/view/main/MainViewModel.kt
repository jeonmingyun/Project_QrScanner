package com.mamp.qrscanner.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamp.qrscanner.view.base.BaseViewModel

class MainViewModel: BaseViewModel() {
    private val _count = MutableLiveData<Int>(0)
    val count: LiveData<Int> = _count

    fun onButtonClicked() {
        _count.value = _count.value!! + 1
    }
}