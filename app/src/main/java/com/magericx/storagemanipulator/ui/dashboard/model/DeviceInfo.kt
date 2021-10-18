package com.magericx.storagemanipulator.ui.dashboard.model

data class DeviceInfo(
        val deviceName: String,
        val manufacturer: String,
        val operatingVersion: String,
        val totalMemory: Long,
        val availMemory: Long,
        val releaseVersion: String
)
