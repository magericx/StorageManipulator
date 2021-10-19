package com.magericx.storagemanipulator.repository

import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.core.content.ContextCompat.getExternalFilesDirs
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.utility.SizeUtil
import java.io.File


class ExternalStorageRepository : SizeRetrieval {

    companion object {
        const val TAG = "ExternalStorageRepository"
    }

    private val externalDataDirectory1: File by lazy {
        //return@lazy Environment.getExternalStorageDirectory()
        return@lazy File(
            StorageManipulatorApplication.instance.applicationContext.getExternalFilesDir(
                "StorageManipulator"
            )!!.path
        )
    }

    //TODO fix logic for external storage
    val externalDataDirectory = StorageManipulatorApplication.instance.applicationContext.getExternalFilesDirs(
        "StorageManipulator"
    )
    fun checkExternalAvailable(): Boolean {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
    }

    override fun getTotalMaxCapacity(): Long {
        if (!checkExternalAvailable()) {
            return 0
        }
        return try {
            Log.d(TAG, "Test here ${externalDataDirectory[1].path}")
            val stat = StatFs(externalDataDirectory[1].path)
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
            val stat = StatFs(externalDataDirectory[1].path)
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
}