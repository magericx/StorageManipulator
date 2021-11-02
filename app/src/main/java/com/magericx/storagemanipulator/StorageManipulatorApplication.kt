package com.magericx.storagemanipulator

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StorageManipulatorApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        poolThread = Executors.newFixedThreadPool(1)
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    }

    companion object {
        const val TAG = "StorageManipulatorApplication"
        lateinit var instance: StorageManipulatorApplication
            private set
        lateinit var poolThread: ExecutorService
            private set
        lateinit var mainThreadHandler: Handler
            private set
    }

}