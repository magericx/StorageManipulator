package com.magericx.storagemanipulator.ui.internal_storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.repository.InternalStorageRepository
import com.magericx.storagemanipulator.ui.internal_storage.model.InternalStorageInfo

class InternalStorageViewModel : ViewModel() {

    private var internalRepository: InternalStorageRepository = InternalStorageRepository()
    private val _internalStorageInfo = MutableLiveData<InternalStorageInfo>()
    val internalStorageInfoObserver: LiveData<InternalStorageInfo> = _internalStorageInfo
    private val poolThread = StorageManipulatorApplication.poolThread
    private val mainHandler = StorageManipulatorApplication.mainThreadHandler


    fun setFirstScreenInfo() {
        poolThread.submit {
            mainHandler.post {
                _internalStorageInfo.apply {
                    value = InternalStorageInfo(
                        availableStorage = internalRepository.getAvailCapacity(),
                        maximumStorage = internalRepository.getTotalMaxCapacity(),
                        inUsedCapacityPercent = internalRepository.getInusedCapacityInPercent()
                    )
                }
            }
        }
    }
}