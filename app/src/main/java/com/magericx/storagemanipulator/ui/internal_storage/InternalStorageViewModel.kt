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
import java.lang.ref.WeakReference

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
    private val weakProgressCallback: WeakReference<ProgressListener>
        get() = WeakReference(progressCallback)


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
                val bytesToGenerate = if (max) {
                    internalRepository.getAvailCapacity()
                } else {
                    SizeUtil.getCapacityToBytes(size, unit)
                }
                internalRepository.writeIntoFiles(
                    bytesToGenerate,
                    weakProgressCallback
                )
                Log.d(TAG, "Finished here")
//                _generateFilesInfo.value = GenerateFilesInfo(
//                    status = GenerateStatus.COMPLETED
//                )
            } catch (e: Exception) {
                Log.d(TAG, "Failed due to ${e}")
                _generateFilesInfo.value = GenerateFilesInfo(
                    status = GenerateStatus.UNKNOWN
                )
            } finally {
                isJobRunning = false
            }

        }
    }

    fun pauseGenerate(){
        internalRepository.pauseGenerate()
    }


    //refresh feature
    fun refreshAll(unit: UnitStatus) {
        _generateFilesInfo.value = GenerateFilesInfo()
        getInternalStorageInfo(unit)
    }

    //callback to update progress status
    private val progressCallback: ProgressListener = object : ProgressListener {
        override fun updateProgress(
            progress: Double,
            addProgressInfo: AddProgressInfo
        ) {
            Log.d(TAG, "Updating progress of $progress")
            val currentStatus: GenerateStatus = if (progress >= 100.0) GenerateStatus.COMPLETED else
                GenerateStatus.INPROGRESS
            mainHandler.post {
                _generateFilesInfo.value =
                    GenerateFilesInfo(
                        status = currentStatus,
                        progressStatus = progress,
                        addProgressInfo = addProgressInfo
                    )
            }
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
    val addProgressInfo: AddProgressInfo? = null
)


//classes for updating generating progress
interface ProgressListener {
    fun updateProgress(progress: Double = 0.0, addProgressInfo: AddProgressInfo)
}

data class AddProgressInfo(
    val addedSize: Long = 0,
    val totalGenerateSize: Long
)