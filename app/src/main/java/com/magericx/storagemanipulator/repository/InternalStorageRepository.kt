package com.magericx.storagemanipulator.repository

import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.magericx.storagemanipulator.ui.internal_storage.ProgressListener
import com.magericx.storagemanipulator.utility.FileIoUtil
import com.magericx.storagemanipulator.utility.SizeUtil
import com.magericx.storagemanipulator.utility.StringUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class InternalStorageRepository : SizeRetrieval {

    companion object {
        const val TAG = "InternalStorageRepository"
    }

    private val fileHelper = FileIoUtil()

    private val dataDirectory: File by lazy {
        return@lazy Environment.getDataDirectory()
    }

    override fun getTotalMaxCapacity(): Long {
        return try {
            Log.d(TAG, "Test here ${dataDirectory.path}")
            val stat = StatFs(dataDirectory.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            Log.d(TAG, "Retrieved getTotalMaxCapacity here ${totalBlocks * blockSize}")
            totalBlocks * blockSize
        } catch (e: Exception) {
            Log.e(TAG, "getTotalMaxCapacity: $e")
            0
        }
        //return SizeUtil.formatSizeDynamically(totalBlocks * blockSize)
    }

    override fun getAvailCapacity(): Long {
        return try {
            val stat = StatFs(dataDirectory.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks = stat.availableBlocksLong
            Log.d(TAG, "Retrieved getAvailCapacity here ${totalBlocks * blockSize}")
            return totalBlocks * blockSize
        } catch (e: Exception) {
            Log.e(TAG, "getAvailCapacity: $e")
            0
        }
        //return SizeUtil.formatSizeDynamically(totalBlocks * blockSize)
    }

    override fun getAvailCapacityInPercent(): Double {
        if (getAvailCapacity() == 0L && getTotalMaxCapacity() == 0L) {
            return -1.0
        }
        Log.d(
            TAG,
            "Retrieved getAvailCapacityInPercent here ${getAvailCapacity() / getTotalMaxCapacity().toDouble() * 100}"
        )
        return getAvailCapacity() / getTotalMaxCapacity().toDouble() * 100
    }

    override fun getInusedCapacityInPercent(): Double {
        return SizeUtil.roundTo1Decimal(100.0 - getAvailCapacityInPercent())
    }

    override suspend fun writeIntoFiles(size: Long, progressListener: WeakReference<ProgressListener>) {
        return withContext(Dispatchers.IO) {
            runInterruptible {
                fileHelper.writeToInternalFile(size, progressListener)
            }
        }
    }
}

interface SizeRetrieval {
    fun getTotalMaxCapacity(): Long
    fun getAvailCapacity(): Long
    fun getAvailCapacityInPercent(): Double
    fun getInusedCapacityInPercent(): Double
    suspend fun writeIntoFiles(size: Long, progressListener: WeakReference<ProgressListener>)
}