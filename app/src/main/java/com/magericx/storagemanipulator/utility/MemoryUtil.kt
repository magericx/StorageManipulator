package com.magericx.storagemanipulator.utility

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import com.magericx.storagemanipulator.StorageManipulatorApplication

object MemoryUtil {
    const val TAG = "MemoryUtil"

    val mi: ActivityManager.MemoryInfo by lazy{
        return@lazy setupMemoryInfo()
    }

    private fun setupMemoryInfo(): ActivityManager.MemoryInfo {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = StorageManipulatorApplication.instance.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        activityManager!!.getMemoryInfo(mi)
        return mi
    }
}