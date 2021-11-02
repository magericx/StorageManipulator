package com.magericx.storagemanipulator.utility

import android.util.Log
import com.magericx.storagemanipulator.ui.internal_storage.UnitStatus
import java.io.File
import java.util.*

object StringUtil {

    const val randomChars: String = "ABCDEFGHIJKLMNOPQRSTUWXYZ1234567890"
    const val TAG = "StringUtil"
    const val filePrefix = "file_"

    fun capitalized(s: String): String {
        if (s.isBlank() || s.isEmpty()) {
            return ""
        }
        val first: Char = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first) + s.substring(1)
        }
    }

    fun generateRandomString(): StringBuilder {
        val randomString: StringBuilder = StringBuilder()
        val randomizer: Random = Random()
        while (randomString.length < 2000) {
            val index: Int = (randomizer.nextFloat() * randomChars.length).toInt()
            //Log.d(TAG, "Random integer here is $index")
            randomString.append(randomChars[index])
        }
        //Log.d(TAG, "Return string here ${randomString}")
        return randomString
    }

    //get the second element from the filename, file_5
    fun getFileName(file: File): String {
        val splittedFileName = file.name.split("_")
        return splittedFileName[1]
    }

    //create the next filename based on previous, using rolling number
    fun getNextFileName(file: File): String {
        val fileIndex = getFileName(file).toInt()
        return filePrefix + fileIndex.inc()
    }

    fun getIntegerMappingFromUnitStatus(unitStatus: UnitStatus): Int {
        return when (unitStatus) {
            UnitStatus.B -> 1
            UnitStatus.KB -> 2
            UnitStatus.MB -> 3
            UnitStatus.GB -> 4
        }
    }

    fun getUnitStatusMappingFromInteger(value: Int): UnitStatus {
        return when (value) {
            1 -> UnitStatus.B
            2 -> UnitStatus.KB
            3 -> UnitStatus.MB
            4 -> UnitStatus.GB
            else -> UnitStatus.KB
        }
    }
}