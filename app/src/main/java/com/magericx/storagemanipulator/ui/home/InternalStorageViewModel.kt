package com.magericx.storagemanipulator.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InternalStorageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is ${javaClass.canonicalName} Fragment"
    }
    val text: LiveData<String> = _text
}