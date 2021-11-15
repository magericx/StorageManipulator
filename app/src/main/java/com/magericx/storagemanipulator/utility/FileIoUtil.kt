package com.magericx.storagemanipulator.utility

import android.util.Log
import com.magericx.storagemanipulator.BuildConfig
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.handler.ExternalProgressManager
import com.magericx.storagemanipulator.handler.InternalProgressManager
import com.magericx.storagemanipulator.handler.ProgressHandler
import com.magericx.storagemanipulator.handler.ProgressManager
import com.magericx.storagemanipulator.performance.PerformanceHelper
import com.magericx.storagemanipulator.ui.internal_storage.ProgressListener
import com.magericx.storagemanipulator.ui.internal_storage.model.AddProgressInfo
import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.ref.WeakReference

class FileIoUtil {

    companion object {
        private const val internalStorage = "storage_manipulator_files"
        private const val sizeOfEachFileBytes = 1000 * 1024 * 1024 //converted from MB to bytes
        private const val TAG = "FileIoUtil"
        private const val maxPercent: Double = 100.0
        private const val numOfJobs = 20
    }

    private lateinit var jobQueue: List<StringBuilder>
    private val trace: PerformanceHelper? = if (BuildConfig.DEBUG) PerformanceHelper() else null

    //Using IO thread from coroutine
    suspend fun writeToInternalFile(
        isInternalDir: Boolean,
        sizeToGenerate: Long,
        progressListener: WeakReference<ProgressListener>
    ) {
        withContext(Dispatchers.IO) {
            runInterruptible {
                trace?.start()
                val progressManager = getProgressManager(isInternalDir)
                var totalGenerateSize = sizeToGenerate
                val directory = getDirectory(isInternalDir = isInternalDir)
                val listener = progressListener.get()
                //first callback here to set the starting mark
                listener?.updateProgress(addProgressInfo = AddProgressInfo(totalGenerateSize = sizeToGenerate))
                createJobQueue()
                while (isActive) {
                    if (!progressManager.getProgressStatus()) {
                        //nothing more to generate
                        if (totalGenerateSize <= 0) {
                            trace?.stop()
                            break
                        }
                        //create new file if current file exceed sizeOfEachFileBytes
                        try {
                            val fileToFill =
                                if (getLastFileInDirectory(directory).length() < sizeOfEachFileBytes) getLastFileInDirectory(
                                    directory
                                ) else getFile(
                                    directory,
                                    StringUtil.getNextFileName(getLastFileInDirectory(directory))
                                )
                            writeIntoFile(fileToFill).let {
                                totalGenerateSize -= it
                            }
                            updateProgressPercent(totalGenerateSize, sizeToGenerate, listener)
                        } catch (e: Exception) {
                            trace?.stop()
                            Log.e(TAG, "Encountered exception here $e")
                            listener?.updateProgress(
                                progress = maxPercent,
                                addProgressInfo = AddProgressInfo(
                                    addedSize = sizeToGenerate,
                                    totalGenerateSize = sizeToGenerate
                                )

                            )
                            break
                        }
                    }
                }
            }
        }

    }

    private fun createJobQueue() {
        val randomString = StringUtil.generateRandomString()
        jobQueue = List(numOfJobs){
            randomString
        }
    }

    private fun getDirectory(isInternalDir: Boolean = false): File {
        //automatically create a parent directory if not exists
        val rootFile = if (isInternalDir) {
            File(StorageManipulatorApplication.instance.filesDir, internalStorage)
        } else {
            StorageManipulatorApplication.instance.applicationContext.getExternalFilesDirs(
                "StorageManipulator"
            )[1]
        }
        if (!rootFile.exists()) {
            rootFile.mkdir()
        }
        return rootFile
    }

    private fun getLastFileInDirectory(parentDirectory: File): File {
        //file_5, file_4,file_3, file_2, file_1, get file_5 in this case
        // Spawn file_1 if there are no children file yet
        return parentDirectory.listFiles()?.maxOrNull() ?: File(parentDirectory, "file_1")
    }


    private fun getFile(parentDirectory: File, childrenFileName: String): File {
        return File(parentDirectory, childrenFileName)
    }

    private fun writeIntoFile(file: File): Long {
        //return in bytes
        val sizeBefore = file.length()
        val fileWriter = BufferedWriter(FileWriter(file, true),32768)
        try {
            jobQueue.forEach {
                fileWriter.write(it.toString())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Exception here $e")
        } finally {
            fileWriter.apply{
                close()
            }
        }
        return file.length() - sizeBefore
    }

    //get remaining to be added percentage first, deduct by 100 to get added percentage
    private fun updateProgressPercent(
        addedSize: Long,
        totalGenerateSize: Long,
        listener: ProgressListener?
    ) {
        val generatedSize = totalGenerateSize - addedSize
        SizeUtil.getPercentage(addedSize, totalGenerateSize).let {
            val addedPercent = SizeUtil.roundTo1Decimal(maxPercent - it)
            if (addedPercent >= maxPercent) listener?.updateProgress(
                progress = maxPercent,
                addProgressInfo = AddProgressInfo(
                    addedSize = totalGenerateSize,
                    totalGenerateSize = totalGenerateSize
                )
            ) else listener?.updateProgress(
                progress = addedPercent,
                addProgressInfo = AddProgressInfo(
                    addedSize = generatedSize,
                    totalGenerateSize = totalGenerateSize
                )
            )
        }
    }

    fun pauseGenerate(isInternalDir: Boolean) {
        if (isInternalDir) ProgressHandler.updateInternalPause() else ProgressHandler.updateExternalPause()
    }

    //only support deleting of entire directory for now
    fun deleteFiles(deleteAll: Boolean, isInternalDir: Boolean): Boolean {
        val directory = getDirectory(isInternalDir = isInternalDir)
        return try {
            if (isInternalDir) {
                directory.deleteRecursively()
            } else {
                directory.listFiles()?.forEach {
                    it.delete()
                }
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete directory $directory")
            false
        }
    }

    private fun getProgressManager(isInternalDir: Boolean): ProgressManager {
        return if (isInternalDir) InternalProgressManager() else ExternalProgressManager()
    }
}