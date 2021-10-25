package com.magericx.storagemanipulator.utility

import com.magericx.storagemanipulator.ui.internal_storage.UnitStatus


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

    fun formatSizeKb(size: Long): Double {
        return (size / 1024).toDouble()
    }

    fun formatSizeMb(size: Long): Double {
        return (size / 1024 / 1024).toDouble()
    }

    fun formatSizeGb(size: Long): Double {
        return roundTo1Decimal(size.toDouble() / 1073741824.toDouble())
    }

    fun roundTo1Decimal(value: Double): Double {
        return String.format("%.1f", value).toDouble()
    }

    fun removeDecimalPoint(value: Double): Double {
        return String.format("%.0f", value).toDouble()
    }

    fun getCapacityWithConversion(value: Long, unit: UnitStatus): Double {
        return when (unit) {
            UnitStatus.KB -> removeDecimalPoint(formatSizeKb(value))
            UnitStatus.MB -> removeDecimalPoint(formatSizeMb(value))
            UnitStatus.GB -> formatSizeGb(value)
        }
    }
}