package com.magericx.storagemanipulator.handler

import android.util.Log
import com.magericx.storagemanipulator.ui.internal_storage.ProgressListener
import com.magericx.storagemanipulator.utility.FileIoUtil
import com.magericx.storagemanipulator.utility.SizeUtil
import java.lang.ref.WeakReference

abstract class SizeRetrieval {

    private val TAG = "SizeRetrieval"
    private var fileHelper: FileIoUtil = FileIoUtil()

    abstract fun getTotalMaxCapacity(): Long
    abstract fun getAvailCapacity(): Long
    private fun getAvailCapacityInPercent(): Double {
        if (getAvailCapacity() == 0L && getTotalMaxCapacity() == 0L) {
            return -1.0
        }
        Log.d(
            TAG,
            "Retrieved getAvailCapacityInPercent here ${getAvailCapacity() / getTotalMaxCapacity().toDouble() * 100}"
        )
        return getAvailCapacity() / getTotalMaxCapacity().toDouble() * 100
    }

    fun getInusedCapacityInPercent(): Double {
        return SizeUtil.roundTo1Decimal(100.0 - getAvailCapacityInPercent())
    }

    fun pauseGenerate(isInternalDir: Boolean) {
        fileHelper.pauseGenerate(isInternalDir)
    }

    suspend fun writeIntoFiles(
        isInternalDir: Boolean,
        size: Long,
        progressListener: WeakReference<ProgressListener>
    ) {
        fileHelper.writeToInternalFile(isInternalDir, size, progressListener)
    }

    fun deleteFiles(isInternalDir: Boolean, deleteAll: Boolean): Boolean {
        val status = fileHelper.deleteFiles(deleteAll, isInternalDir)
        Log.d(TAG, "Deleted status is $status")
        return status
    }
}