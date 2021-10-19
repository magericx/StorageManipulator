package com.magericx.storagemanipulator.utility


object SizeUtil {

    fun formatSizeDynamically(size: Long): String {
        var newSize = size
        var suffix: String? = null

        if (newSize >= 1024) {
            suffix = "KB"
            newSize /= 1024
            if (newSize >= 1024) {
                suffix = "MB"
                newSize /= 1024
            }
        }

        val resultBuffer = StringBuilder(newSize.toString())

        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }

        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    fun formatSizeKb(size: Long): Long {
        var newSize = size
        if (newSize >= 1024) {
            newSize /= 1024
        }
        return newSize
    }

    fun formatSizeMb(size: Long): Long {
        var newSize = size
        if (newSize >= 1024) {
            newSize /= 1024
            if (newSize >= 1024) {
                newSize /= 1024
            }
        }
        return newSize
    }

    fun roundTo1Decimal(value: Double): Double {
        return String.format("%.1f", value).toDouble()
    }
}