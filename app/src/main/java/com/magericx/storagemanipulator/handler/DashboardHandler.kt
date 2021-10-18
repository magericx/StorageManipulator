package com.magericx.storagemanipulator.handler

import com.magericx.storagemanipulator.repository.DeviceInfoRepository
import com.magericx.storagemanipulator.repository.InternalStorageRepository
import com.magericx.storagemanipulator.ui.dashboard.model.DeviceInfo

class DashboardHandler(private val deviceInfoRepository: DeviceInfoRepository,
                       private val internalStorageRepository: InternalStorageRepository) {

    fun getFirstScreenInfo(): DeviceInfo {
        return DeviceInfo(deviceName = deviceInfoRepository.getDeviceName(),
                manufacturer = deviceInfoRepository.getManufacturer(),
                operatingVersion = deviceInfoRepository.getOperatingVersion(),
                totalMemory = deviceInfoRepository.getTotalDeviceMemorySize(),
                availMemory = deviceInfoRepository.getAvailDeviceMemorySize(),
                releaseVersion = deviceInfoRepository.getVersionRelease())
    }


}