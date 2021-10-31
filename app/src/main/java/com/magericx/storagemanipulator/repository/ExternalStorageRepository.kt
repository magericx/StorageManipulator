package com.magericx.storagemanipulator.repository

import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.core.content.ContextCompat.getExternalFilesDirs
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.ui.internal_storage.ProgressListener
import com.magericx.storagemanipulator.utility.SizeUtil
import java.io.File
import java.lang.ref.WeakReference


class ExternalStorageRepository : SizeRetrieval {

    companion object {
        const val TAG = "ExternalStorageRepository"
    }

    private val externalDataDirectory: Array<File> =
        StorageManipulatorApplication.instance.applicationContext.getExternalFilesDirs(
            "StorageManipulator"
        )

    fun checkExternalAvailable(): Boolean {
        return externalDataDirectory.filterNotNull().size >= 2
    }

    override fun getTotalMaxCapacity(): Long {
        if (!checkExternalAvailable()) {
            return 0
        }
        return try {
            val dataDirectory: File =
                if (externalDataDirectory.filterNotNull().size >= 2) externalDataDirectory[1] else externalDataDirectory[0]
            val stat = StatFs(dataDirectory.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            Log.d(TAG, "Retrieved getTotalMaxCapacity here ${totalBlocks * blockSize}")
            totalBlocks * blockSize
        } catch (e: Exception) {
            Log.e(TAG, "getTotalMaxCapacity: $e")
            0
        }
    }

    override fun getAvailCapacity(): Long {
        if (!checkExternalAvailable()) {
            return 0
        }
        return try {
            val dataDirectory: File =
                if (externalDataDirectory.filterNotNull().size >= 2) externalDataDirectory[1] else externalDataDirectory[0]
            val stat = StatFs(dataDirectory.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks = stat.availableBlocksLong
            Log.d(TAG, "Retrieved getAvailCapacity here ${totalBlocks * blockSize}")
            return totalBlocks * blockSize
        } catch (e: Exception) {
            Log.e(TAG, "getAvailCapacity: $e")
            0
        }
    }

    override fun getAvailCapacityInPercent(): Double {
        if (getAvailCapacity() == 0L && getTotalMaxCapacity() == 0L) {
            return -1.0
        }
        return getAvailCapacity() / getTotalMaxCapacity().toDouble() * 100
    }

    override fun getInusedCapacityInPercent(): Double {
        return SizeUtil.roundTo1Decimal(100.0 - getAvailCapacityInPercent())
    }

    override fun pauseGenerate() {
        TODO("Not yet implemented")
    }

    override suspend fun writeIntoFiles(size: Long, progressListener: WeakReference<ProgressListener>) {
        TODO("Not yet implemented")
    }

}