package com.magericx.storagemanipulator.ui.external_storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExternalStorageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is ${javaClass.canonicalName} Fragment"
    }
    val text: LiveData<String> = _text
}