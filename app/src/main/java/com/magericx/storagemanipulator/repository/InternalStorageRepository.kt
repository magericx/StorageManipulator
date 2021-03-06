package com.magericx.storagemanipulator.repository

import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.magericx.storagemanipulator.handler.SizeRetrieval
import java.io.File

class InternalStorageRepository : SizeRetrieval() {

    companion object {
        const val TAG = "InternalStorageRepository"
    }

    private val dataDirectory: File by lazy {
        return@lazy Environment.getDataDirectory()
    }

    override fun getTotalMaxCapacity(): Long {
        return try {
            val stat = StatFs(dataDirectory.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            Log.d(TAG, "Retrieved getTotalMaxCapacity here ${totalBlocks * blockSize}")
            totalBlocks * blockSize
        } catch (e: Exception) {
            Log.e(TAG, "Error - getTotalMaxCapacity: $e")
            0
        }
    }

    override fun getAvailCapacity(): Long {
        return try {
            val stat = StatFs(dataDirectory.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks = stat.availableBlocksLong
            Log.d(TAG, "Retrieved getAvailCapacity here ${totalBlocks * blockSize}")
            totalBlocks * blockSize
        } catch (e: Exception) {
            Log.e(TAG, "Error - getAvailCapacity: $e")
            0
        }
    }
}

