package com.magericx.storagemanipulator.ui.internal_storage

import android.util.Log
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
    var isJobRunning: Boolean = false
    private val TAG = "InternalStorageViewModel"

    //observer for generation status
    private val _generateFilesInfo = MutableLiveData<GenerateFilesInfo>()
    val generateFilesInfoObserver: LiveData<GenerateFilesInfo> = _generateFilesInfo


    fun getInternalStorageInfo(unit: UnitStatus) {
        poolThread.submit {
            val availableCapacity: Double =
                SizeUtil.getCapacityWithConversionToUnit(
                    internalRepository.getAvailCapacity(),
                    unit
                )
            val totalCapacity: Double =
                SizeUtil.getCapacityWithConversionToUnit(
                    internalRepository.getTotalMaxCapacity(),
                    unit
                )
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

    fun generateFiles(size: Double = 0.0, max: Boolean = false, unit: UnitStatus = UnitStatus.B) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _generateFilesInfo.apply {
                    value = if (isJobRunning) GenerateFilesInfo(
                        status = GenerateStatus.JOB_CONFLICT,
                        progressStatus = null
                    ) else if (internalRepository.getAvailCapacity() <= 0)
                        GenerateFilesInfo(status = GenerateStatus.FULL_STORAGE)
                    else
                        GenerateFilesInfo(status = GenerateStatus.STARTED, progressStatus = 0.0)
                }
                if (_generateFilesInfo.value != GenerateFilesInfo(
                        status = GenerateStatus.STARTED,
                        progressStatus = 0.0
                    )
                ) return@launch
                Log.d(TAG, "Started job here")
                isJobRunning = true
                if (max) {
                    internalRepository.writeIntoFiles(internalRepository.getAvailCapacity(),progressCallback)
                } else {
                    val bytesToGenerate = SizeUtil.getCapacityToBytes(size, unit)
                    internalRepository.writeIntoFiles(bytesToGenerate,progressCallback)
                }
                println("Finished here")
                _generateFilesInfo.value = GenerateFilesInfo(
                    status = GenerateStatus.COMPLETED
                )
                isJobRunning = false
            } catch (e: Exception) {
                _generateFilesInfo.value = GenerateFilesInfo(
                    status = GenerateStatus.UNKNOWN
                )
            }

        }
    }


    //refresh feature
    fun refreshAll(unit: UnitStatus) {
        _generateFilesInfo.value = GenerateFilesInfo()
        getInternalStorageInfo(unit)
    }

    //callback
    val progressCallback: ProgressListener = object: ProgressListener{
        override fun updateProgress(progress: Int) {
            //TODO update observer whenever callback is received
            Log.d(TAG,"Updating progress of $progress")
        }
    }
}

enum class UnitStatus {
    B, KB, MB, GB
}

enum class GenerateStatus(val status: String) {
    FULL_STORAGE("Insufficient storage to proceed"), COMPLETED("Files have been generated succesfully"), JOB_CONFLICT(
        "Job already started"
    ),
    UNKNOWN("Failed due to unknown reason"), STARTED("Job has started"), INPROGRESS("In Progress")

}

data class GenerateFilesInfo(
    val status: GenerateStatus? = null,
    val progressStatus: Double? = null,
)

interface ProgressListener {
    fun updateProgress(progress: Int)
}