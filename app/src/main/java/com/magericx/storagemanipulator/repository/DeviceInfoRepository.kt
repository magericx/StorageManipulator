package com.magericx.storagemanipulator.repository

import android.os.Build
import com.magericx.storagemanipulator.utility.StringUtil
import java.util.*

class DeviceInfoRepository {

    companion object{
        const val operatingSystem: String = "OS"
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

    fun getDeviceMemorySize(): Long{
        //TODO add implementation
        return 0
    }
}