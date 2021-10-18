package com.magericx.storagemanipulator.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.handler.DashboardHandler
import com.magericx.storagemanipulator.repository.DeviceInfoRepository
import com.magericx.storagemanipulator.repository.InternalStorageRepository
import com.magericx.storagemanipulator.ui.dashboard.model.DeviceInfo

class DashboardViewModel : ViewModel() {

    private var deviceInfoRepository: DeviceInfoRepository = DeviceInfoRepository()
    private var internalStorageRepository: InternalStorageRepository = InternalStorageRepository()
    private val dashboardHandler: DashboardHandler = DashboardHandler(deviceInfoRepository, internalStorageRepository)
    private val poolThread = StorageManipulatorApplication.poolThread
    private val mainHandler = StorageManipulatorApplication.mainThreadHandler

    private val _deviceInfo = MutableLiveData<DeviceInfo>()

    val deviceInfoObserver: LiveData<DeviceInfo> = _deviceInfo

    companion object {
        const val TAG = "DashboardViewModel"
    }

    fun setFirstScreenInfo() {
        var firstScreenInfo: DeviceInfo?
        Log.d(TAG, "Called information here")
        poolThread.submit {
            firstScreenInfo = dashboardHandler.getFirstScreenInfo()
            mainHandler.post {
                _deviceInfo.apply {
                    Log.d(TAG, "Callback observer here")
                    value = firstScreenInfo
                }
            }
        }
    }
}