package com.magericx.storagemanipulator.ui.internal_storage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.handler.ProgressHandler
import com.magericx.storagemanipulator.repository.InternalStorageRepository
import com.magericx.storagemanipulator.ui.internal_storage.model.AddProgressInfo
import com.magericx.storagemanipulator.ui.internal_storage.model.GenerateFilesInfo
import com.magericx.storagemanipulator.ui.internal_storage.model.StorageInfo
import com.magericx.storagemanipulator.utility.SizeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.ref.WeakReference
import kotlin.coroutines.cancellation.CancellationException

class InternalStorageViewModel : ViewModel() {

    companion object {
        private const val TAG = "InternalStorageViewModel"
    }

    private var internalRepository: InternalStorageRepository = InternalStorageRepository()
    private val _internalStorageInfo = MutableLiveData<StorageInfo>()
    val internalStorageInfoObserver: LiveData<StorageInfo> = _internalStorageInfo
    private val poolThread = StorageManipulatorApplication.poolThread
    private val mainHandler = StorageManipulatorApplication.mainThreadHandler
    private var isJobRunning: Boolean = false
    private var currentJob: Job? = null

    //observer for generation status
    private val _generateFilesInfo = MutableLiveData<GenerateFilesInfo>()
    val generateFilesInfoObserver: LiveData<GenerateFilesInfo> = _generateFilesInfo

    //observer for deletion status
    private val _deleteFilesInfo = MutableLiveData<DeleteStatus>()
    val deleteFilesInfoObserver: LiveData<DeleteStatus> = _deleteFilesInfo

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
            val inUsedCapacityPercent = internalRepository.getInUsedCapacityInPercent()
            mainHandler.post {
                _internalStorageInfo.apply {
                    value = StorageInfo(
                        availableStorage = availableCapacity,
                        maximumStorage = totalCapacity,
                        inUsedCapacityPercent = inUsedCapacityPercent
                    )
                }
            }
        }
    }

    fun generateFiles(size: Double = 0.0, max: Boolean = false, unit: UnitStatus = UnitStatus.B) {
        currentJob?.let {
            //Job is running, whether on pause or other state
            if (it.isActive) {
                _generateFilesInfo.apply {
                    value = GenerateFilesInfo(
                        status = GenerateStatus.JOB_CONFLICT,
                        progressStatus = null
                    )
                }
                return
            }
        }
        //if there is no existing job running, assign a new job
        currentJob = viewModelScope.launch(Dispatchers.Main) {
            try {
                _generateFilesInfo.apply {
                    value =
                        if (internalRepository.getAvailCapacity() <= 0)
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
                    isInternalDir = true,
                    bytesToGenerate,
                    weakProgressCallback
                )
                Log.d(TAG, "Finished here")
            } catch (e: IOException) {
                Log.e(TAG, "Failed due to $e")
                _generateFilesInfo.value = GenerateFilesInfo(
                    status = GenerateStatus.UNKNOWN
                )
            } finally {
                isJobRunning = false
            }
        }
        currentJob?.invokeOnCompletion {
            Log.d(TAG,"Finished currentJob here")
            if (it is CancellationException) {
                Log.e(TAG, "Cancelled by user here")
                _generateFilesInfo.value = GenerateFilesInfo(status = GenerateStatus.CANCELLED)
                ProgressHandler.resetInternalPauseStatus()
            }
        }
    }

    fun pauseGenerate() {
        internalRepository.pauseGenerate(isInternalDir = true)
    }

    fun cancelGenerate() {
        currentJob?.cancel()
    }

    //refresh feature
    fun refreshAll(unit: UnitStatus) {
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

    fun deleteFiles(deleteAll: Boolean = true) {
        var deleteStatus: Boolean
        poolThread.submit {
            if (currentJob?.isActive == true) {
                _deleteFilesInfo.postValue(DeleteStatus.CONFLICT)
                return@submit
            }
            deleteStatus = internalRepository.deleteFiles(isInternalDir = true, deleteAll)
            mainHandler.post {
                if (deleteStatus) {
                    _deleteFilesInfo.value = DeleteStatus.SUCCESS
                } else {
                    _deleteFilesInfo.value = DeleteStatus.FAILED
                }
            }
        }
    }
}

enum class UnitStatus {
    B, KB, MB, GB
}

enum class GenerateStatus(val status: String) {
    FULL_STORAGE("Insufficient storage to proceed"), COMPLETED("Files have been generated successfully"), JOB_CONFLICT(
        "Job already started"
    ),
    UNKNOWN("Failed due to unknown reason"), STARTED("Job has started"), INPROGRESS("In Progress"), CANCELLED(
        "Job has been cancelled"
    )

}

enum class DeleteStatus(val status: String) {
    SUCCESS("Files deleted successfully"), FAILED("Failed to delete files"), CONFLICT("Remove/complete existing job first")
}

//classes for updating generating progress
interface ProgressListener {
    fun updateProgress(progress: Double = 0.0, addProgressInfo: AddProgressInfo)
}

