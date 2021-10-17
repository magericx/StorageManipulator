package com.magericx.storagemanipulator.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magericx.storagemanipulator.StorageManipulatorApplication
import com.magericx.storagemanipulator.handler.DashboardHandler
import com.magericx.storagemanipulator.repository.DeviceInfoRepository
import com.magericx.storagemanipulator.ui.dashboard.model.DeviceInfo

class DashboardViewModel : ViewModel() {

    private var deviceInfoRepository: DeviceInfoRepository = DeviceInfoRepository()
    private val dashboardHandler: DashboardHandler = DashboardHandler(deviceInfoRepository)
    private val poolThread = StorageManipulatorApplication.poolThread
    private val mainHandler = StorageManipulatorApplication.mainThreadHandler
    
    private val _deviceInfo = MutableLiveData<DeviceInfo>()

    val deviceInfoObserver: LiveData<DeviceInfo> = _deviceInfo

    fun setFirstScreenInfo() {
        var firstScreenInfo: DeviceInfo?
        poolThread.submit {
            firstScreenInfo = dashboardHandler.getFirstScreenInfo()
            mainHandler.post {
                _deviceInfo.apply {
                    value = firstScreenInfo
                }
            }
        }
    }
}