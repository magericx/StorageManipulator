package com.magericx.storagemanipulator.utility

import android.util.Log
import com.magericx.storagemanipulator.StorageManipulatorApplication
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class FileIoUtil {

    companion object {
        private const val internalStorage = "storage_manipulator_files"
        private const val sizeOfEachFileBytes = 500 * 1024 * 1024 //converted from MB to bytes
        private const val TAG = "FileIoUtil"
    }

    private val internalPause = false
    private val jobQueue: MutableList<StringBuilder> = mutableListOf()

    fun writeToInternalFile(sizeToGenerate: Long) {
        var totalGenerateSize = sizeToGenerate
        val directory = getDirectory(isInternalDir = true)
        while (true) {
            Log.d(TAG,"Size here is ${sizeToGenerate}")
            //nothing more to generate
            if (totalGenerateSize <= 0) break
            if (jobQueue.size <= 10) {
                jobQueue.add(StringUtil.generateRandomString())
                continue
            }
            //create new file if current file exceed sizeOfEachFileBytes
            val fileToFill =
                if (getLastFileInDirectory(directory).length() < sizeOfEachFileBytes) getLastFileInDirectory(
                    directory
                ) else getFile(
                    directory,
                    StringUtil.getNextFileName(getLastFileInDirectory(directory))
                )
            totalGenerateSize -= writeIntoFile(fileToFill)
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
        if (!rootFile.exists()){
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
        //Log.d(TAG, "Writing into file $file")
        val sizeBefore = file.length()
//        try{
            val fileWriter = BufferedWriter(FileWriter(file, true))
            jobQueue.forEach {
                fileWriter.append(it.toString())
                //Log.d(TAG, "Writing ${it} here")
            }
            jobQueue.clear()
            fileWriter.flush()
            Log.d(TAG, "Size before is ${sizeBefore} and after is ${file.length()}")
//        }catch(e:Exception){
//            Log.e(TAG,"Exception here $e")
//        }
        return file.length() - sizeBefore

    }
}