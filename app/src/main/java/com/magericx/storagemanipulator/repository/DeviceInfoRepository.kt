package com.magericx.storagemanipulator.repository

import android.os.Build
import android.util.Log
import com.magericx.storagemanipulator.utility.MemoryUtil
import com.magericx.storagemanipulator.utility.StringUtil
import java.util.Locale


class DeviceInfoRepository {

    companion object {
        const val operatingSystem: String = "OS"
        const val TAG = "DeviceInfoRepository"
    }

    fun getDeviceName(): String {
        val manufacturer: String = Build.MANUFACTURER
        val model: String = Build.MODEL
        return if (model.toLowerCase(Locale.ROOT).startsWith(manufacturer.toLowerCase(Locale.ROOT))) {
            StringUtil.capitalized(model)
        } else {
            "${StringUtil.capitalized(manufacturer)}  $model"
        }
    }

    fun getManufacturer(): String {
        return StringUtil.capitalized(Build.MANUFACTURER)
    }

    fun getOperatingVersion(): String {
        return "$operatingSystem  ${Build.VERSION.SDK_INT}"
    }

    fun getVersionRelease(): String {
        return Build.VERSION.RELEASE
    }

    fun getTotalDeviceMemorySize(): Long {
        return try {
            MemoryUtil.mi.totalMem / 0x100000L
        } catch (e: Exception) {
            Log.e(TAG, "Encountered error $e")
            0
        }
    }

    fun getAvailDeviceMemorySize(): Long {
        return try {
            MemoryUtil.mi.availMem / 0x100000L
        } catch (e: Exception) {
            Log.e(TAG, "Encountered error $e")
            0
        }
    }
}