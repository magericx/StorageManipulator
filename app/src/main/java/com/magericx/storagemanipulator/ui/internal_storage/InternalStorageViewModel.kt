package com.magericx.storagemanipulator.ui.internal_storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.repository.InternalStorageRepository
import com.magericx.storagemanipulator.ui.internal_storage.model.InternalStorageInfo
import com.magericx.storagemanipulator.utility.SizeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternalStorageViewModel : ViewModel() {

    private var internalRepository: InternalStorageRepository = InternalStorageRepository()
    private val _internalStorageInfo = MutableLiveData<InternalStorageInfo>()
    val internalStorageInfoObserver: LiveData<InternalStorageInfo> = _internalStorageInfo
    private val poolThread = StorageManipulatorApplication.poolThread
    private val mainHandler = StorageManipulatorApplication.mainThreadHandler


    fun getInternalStorageInfo(unit: UnitStatus) {
        poolThread.submit {
            val availableCapacity: Double =
                SizeUtil.getCapacityWithConversion(internalRepository.getAvailCapacity(), unit)
            val totalCapacity: Double =
                SizeUtil.getCapacityWithConversion(internalRepository.getTotalMaxCapacity(), unit)
            val inUsedCapacityPercent = internalRepository.getInusedCapacityInPercent()
            mainHandler.post {
                _internalStorageInfo.apply {
                    value = InternalStorageInfo(
                        availableStorage = availableCapacity,
                        maximumStorage = totalCapacity,
                        inUsedCapacityPercent = inUsedCapacityPercent
                    )
                }
            }
        }
    }

    fun generateFiles(size: Long = 0, max: Boolean = false) {
        viewModelScope.launch(Dispatchers.Main) {
            if (max){
                internalRepository.writeIntoFiles(internalRepository.getAvailCapacity())
            }else{
                internalRepository.writeIntoFiles(size)
            }
            println("Finished here")
        }
    }
}

enum class UnitStatus {
    KB, MB, GB
}