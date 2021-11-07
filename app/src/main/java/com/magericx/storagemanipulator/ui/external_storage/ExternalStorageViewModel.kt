package com.magericx.storagemanipulator.ui.external_storage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.handler.ProgressHandler
import com.magericx.storagemanipulator.repository.ExternalStorageRepository
import com.magericx.storagemanipulator.ui.internal_storage.DeleteStatus
import com.magericx.storagemanipulator.ui.internal_storage.GenerateStatus
import com.magericx.storagemanipulator.ui.internal_storage.ProgressListener
import com.magericx.storagemanipulator.ui.internal_storage.UnitStatus
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

class ExternalStorageViewModel : ViewModel() {

    companion object {
        private val TAG = "InternalStorageViewModel"
    }

    private var externalRepository: ExternalStorageRepository = ExternalStorageRepository()

    private val poolThread = StorageManipulatorApplication.poolThread
    private val mainHandler = StorageManipulatorApplication.mainThreadHandler

    private val weakProgressCallback: WeakReference<ProgressListener>
        get() = WeakReference(progressCallback)

    private val _externalStorageInfo = MutableLiveData<StorageInfo>()
    val externalStorageInfoObserver: LiveData<StorageInfo> = _externalStorageInfo

    var isJobRunning: Boolean = false
    private var currentJob: Job? = null

    //observer for generation status
    private val _generateFilesInfo = MutableLiveData<GenerateFilesInfo>()
    val generateFilesInfoObserver: LiveData<GenerateFilesInfo> = _generateFilesInfo

    //observer for deletion status
    private val _deleteFilesInfo = MutableLiveData<DeleteStatus>()
    val deleteFilesInfoObserver: LiveData<DeleteStatus> = _deleteFilesInfo

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

    fun getExternalStorageInfo(unit: UnitStatus) {
        poolThread.submit {
            val availableCapacity: Double =
                SizeUtil.getCapacityWithConversionToUnit(
                    externalRepository.getAvailCapacity(),
                    unit
                )
            val totalCapacity: Double =
                SizeUtil.getCapacityWithConversionToUnit(
                    externalRepository.getTotalMaxCapacity(),
                    unit
                )
            val inUsedCapacityPercent = externalRepository.getInusedCapacityInPercent()
            mainHandler.post {
                _externalStorageInfo.apply {
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
        currentJob = viewModelScope.launch(Dispatchers.Main) {
            try {
                _generateFilesInfo.apply {
                    value = if (isJobRunning || ProgressHandler.internalPause) GenerateFilesInfo(
                        status = GenerateStatus.JOB_CONFLICT,
                        progressStatus = null
                    ) else if (externalRepository.getAvailCapacity() <= 0)
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
                    externalRepository.getAvailCapacity()
                } else {
                    SizeUtil.getCapacityToBytes(size, unit)
                }
                externalRepository.writeIntoFiles(
                    isInternalDir = false,
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
            if (it is CancellationException) {
                Log.e(TAG, "Cancelled by user here")
                _generateFilesInfo.value = GenerateFilesInfo(status = GenerateStatus.CANCELLED)
                ProgressHandler.resetExternalPauseStatus()
            }
        }
    }

    //refresh feature
    fun refreshAll(unit: UnitStatus) {
        getExternalStorageInfo(unit)
    }

    fun deleteFiles(deleteAll: Boolean = true) {
        var deleteStatus: Boolean
        poolThread.submit {
            if (isJobRunning || ProgressHandler.internalPause) {
                _deleteFilesInfo.postValue(DeleteStatus.CONFLICT)
                return@submit
            }
            deleteStatus = externalRepository.deleteFiles(isInternalDir = false, deleteAll)
            mainHandler.post {
                if (deleteStatus) {
                    _deleteFilesInfo.value = DeleteStatus.SUCCESS
                } else {
                    _deleteFilesInfo.value = DeleteStatus.FAILED
                }
            }
        }
    }

    fun pauseGenerate() {
        externalRepository.pauseGenerate(isInternalDir = false)
    }

    fun cancelGenerate() {
        currentJob?.cancel()
    }
}