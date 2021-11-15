package com.magericx.storagemanipulator.utility

import java.io.File
import java.util.*

object StringUtil {

    private const val randomChars: String = "ABCDEFGHIJKLMNOPQRSTUWXYZ1234567890"
    const val TAG = "StringUtil"
    private const val filePrefix = "file_"

    private const val stringLength = 1000000L

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
        val randomize = Random()
        while (randomString.length < stringLength) {
            val index: Int = (randomize.nextFloat() * randomChars.length).toInt()
            randomString.append(randomChars[index])
        }
        return randomString
    }

    //get the second element from the filename, file_5
    private fun getFileName(file: File): String {
        val delimitedFileName = file.name.split("_")
        return delimitedFileName[1]
    }

    //create the next filename based on previous, using rolling number
    fun getNextFileName(file: File): String {
        val fileIndex = getFileName(file).toInt()
        return filePrefix + fileIndex.inc()
    }

}