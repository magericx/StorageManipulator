package com.magericx.storagemanipulator.performance

import android.util.Log
import com.magericx.storagemanipulator.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

class PerformanceHelper {
    companion object {
        const val TAG = "PerformanceHelper"
    }

    private var startTime: Long = 0
    private var endTime: Long = 0
    private val simpleDateFormat: SimpleDateFormat by lazy {
        return@lazy SimpleDateFormat("HH:mm", Locale.ENGLISH)
    }

    /**
     * Start the timer
     */
    fun start() {
        startTime = getCurrentTime()
        Log.d(TAG, "Trace - Start time is ${simpleDateFormat.format(Date())}")
    }

    /**
     * Stop the timer and collect metrics
     */
    fun stop() {
        try {
            if (BuildConfig.DEBUG && startTime == 0L) {
                error("Assertion failed")
            }
            endTime = getCurrentTime()
            Log.d(TAG, "Trace - Finish time is ${simpleDateFormat.format(Date())}")
            val elapsedTime = calculateElapse(TimeFormat.MILLISECONDS)
            Log.d(TAG, "Trace - Time elapsed is $elapsedTime")
        } catch (e: Exception) {
            when (e) {
                is AssertionError, is IllegalAccessError -> Log.e(
                    TAG,
                    "Trace - Start not called yet!"
                )
            }
        }
    }

    private fun getCurrentTime(): Long {
        return Date().time
    }

    private fun calculateElapse(timeFormat: TimeFormat): Long {
        val diff = endTime - startTime
        return when (timeFormat) {
            TimeFormat.MILLISECONDS -> diff
            TimeFormat.SECONDS -> diff / 1000
            TimeFormat.MINUTES -> diff / 1000 / 60
            TimeFormat.HOURS -> diff / 1000 / 60 / 60
        }
    }
}

enum class TimeFormat {
    MILLISECONDS, SECONDS, MINUTES, HOURS
}

