package com.magericx.storagemanipulator.utility

import android.util.Log
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.ui.internal_storage.ProgressListener
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileIoUtil {

    companion object {
        private const val internalStorage = "storage_manipulator_files"
        private const val sizeOfEachFileBytes = 500 * 1024 * 1024 //converted from MB to bytes
        private const val TAG = "FileIoUtil"
    }

    private val internalPause = false
    private val jobQueue: MutableList<StringBuilder> = mutableListOf()

    fun writeToInternalFile(sizeToGenerate: Long, progressListener: ProgressListener) {
        var totalGenerateSize = sizeToGenerate
        val directory = getDirectory(isInternalDir = true)
        val randomString = StringUtil.generateRandomString()
        var testProgress = 0
        while (true) {
            Log.d(TAG, "Size here is ${sizeToGenerate}")
            //nothing more to generate
            if (totalGenerateSize <= 0) break
            if (jobQueue.size <= 10) {
                jobQueue.add(randomString)
                continue
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
                totalGenerateSize -= writeIntoFile(fileToFill)
                //todo update callback to weakreference and link to observer
                progressListener.updateProgress(testProgress)
                //test
                testProgress += 5
            } catch (e: Exception) {
                Log.e(TAG, "Encountered exception here $e")
                break
            }
        }
    }

    fun getDirectory(isInternalDir: Boolean = false): File {
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

    fun getLastFileInDirectory(parentDirectory: File): File {
        //file_5, file_4,file_3, file_2, file_1, get file_5 in this case
        // Spawn file_1 if there are no children file yet
        val lastFile = parentDirectory.listFiles()?.maxOrNull() ?: File(parentDirectory, "file_1")
        return lastFile
    }


    fun getFile(parentDirectory: File, childrenFileName: String): File {
        val childrenFile = File(parentDirectory, childrenFileName)
        return childrenFile
    }

    private fun writeIntoFile(file: File): Long {
        //return in bytes
        val sizeBefore = file.length()
        val fileWriter = BufferedWriter(FileWriter(file, true))
        try {
            jobQueue.forEach {
                fileWriter.append(it.toString())
            }
            jobQueue.clear()
            fileWriter.flush()
            Log.d(TAG, "Size before is ${sizeBefore} and after is ${file.length()}")
        } catch (e: IOException) {
            Log.e(TAG, "Exception here $e")
        } finally {
            fileWriter.close()
        }
        return file.length() - sizeBefore

    }
}