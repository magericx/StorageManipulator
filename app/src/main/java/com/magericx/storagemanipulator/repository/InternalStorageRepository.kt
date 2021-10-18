package com.magericx.storagemanipulator.repository

import android.icu.text.StringPrepParseException
import android.os.Environment
import android.os.StatFs
import com.magericx.storagemanipulator.utility.SizeUtil
import java.io.File

class InternalStorageRepository: SizeRetrieval {

    override fun getTotalMaxCapacity(): String{
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize: Long = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return SizeUtil.formatSizeDynamically(totalBlocks * blockSize)
    }

    override fun getAvailCapacity(): String{
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize: Long = stat.blockSizeLong
        val totalBlocks = stat.availableBlocksLong
        return SizeUtil.formatSizeDynamically(totalBlocks * blockSize)
    }

    override fun getAvailCapacityInPercent(): Double {
        TODO("Not yet implemented")
    }
}

interface SizeRetrieval{
    fun getTotalMaxCapacity(): String
    fun getAvailCapacity(): String
    fun getAvailCapacityInPercent(): Double
}