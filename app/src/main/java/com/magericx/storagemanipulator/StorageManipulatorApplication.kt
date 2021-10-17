package com.magericx.storagemanipulator

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StorageManipulatorApplication: Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }
    companion object {
        const val TAG = "StorageManipulatorApplication"
        lateinit var instance: StorageManipulatorApplication
            private set
        val poolThread: ExecutorService =  Executors.newFixedThreadPool(2)
        val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    }

}