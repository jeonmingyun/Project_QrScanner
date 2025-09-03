package com.mamp.qrscanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrDataViewModel: ViewModel() {
    private val _count = MutableLiveData<Int>(0)
    val count: LiveData<Int> = _count

    fun onButtonClicked() {
        val current = _count.value ?: 0
        _count.value = current + 1
    }
}