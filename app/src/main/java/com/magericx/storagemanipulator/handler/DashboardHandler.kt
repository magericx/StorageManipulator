package com.magericx.storagemanipulator.handler

import com.magericx.storagemanipulator.repository.DeviceInfoRepository
import com.magericx.storagemanipulator.ui.dashboard.model.DeviceInfo

class DashboardHandler(private val deviceInfoRepository: DeviceInfoRepository) {

    fun getFirstScreenInfo(): DeviceInfo {
        return DeviceInfo(deviceName = deviceInfoRepository.getDeviceName(), manufacturer = deviceInfoRepository.getManufacturer(),
                operatingVersion = deviceInfoRepository.getOperatingVersion())
    }


}