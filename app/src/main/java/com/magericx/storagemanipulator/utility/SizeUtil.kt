package com.magericx.storagemanipulator.utility

import com.magericx.storagemanipulator.ui.internal_storage.UnitStatus


object SizeUtil {
    const val TAG = "SizeUtil"

    private fun formatSizeKb(size: Long): Double {
        return (size / 1024).toDouble()
    }

    private fun formatSizeMb(size: Long): Double {
        return (size / 1024 / 1024).toDouble()
    }

    private fun formatSizeGb(size: Long): Double {
        return roundTo1Decimal(size.toDouble() / 1073741824.toDouble())
    }

    fun roundTo1Decimal(value: Double): Double {
        return String.format("%.1f", value).toDouble()
    }

    private fun removeDecimalPoint(value: Double): Double {
        return String.format("%.0f", value).toDouble()
    }

    private fun formatKbToBytes(size: Double): Double {
        return (size * 1024)
    }

    private fun formatMbToBytes(size: Double): Double {
        return (size * 1024 * 1024)
    }

    private fun formatGbToBytes(size: Double): Double {
        return (size * 1024 * 1024 * 1024)
    }


    //convert bytes to the unitStatus passed in
    fun getCapacityWithConversionToUnit(value: Long, unit: UnitStatus): Double {
        return when (unit) {
            UnitStatus.KB -> removeDecimalPoint(formatSizeKb(value))
            UnitStatus.MB -> removeDecimalPoint(formatSizeMb(value))
            UnitStatus.GB -> formatSizeGb(value)
            UnitStatus.B -> value.toDouble()
        }
    }

    fun getCapacityToBytes(size: Double, unit: UnitStatus): Long {
        return when (unit) {
            UnitStatus.B -> size.toLong()
            UnitStatus.KB -> formatKbToBytes(size).toLong()
            UnitStatus.MB -> formatMbToBytes(size).toLong()
            UnitStatus.GB -> formatGbToBytes(size).toLong()
        }
    }

    fun getPercentage(firstValue: Long, secondValue: Long): Double {
        //Log.d(TAG, "getPercentage - remaining percentage is ${(firstValue.toDouble() / secondValue.toDouble()) * 100}")
        return (firstValue.toDouble() / secondValue.toDouble()) * 100
    }
}