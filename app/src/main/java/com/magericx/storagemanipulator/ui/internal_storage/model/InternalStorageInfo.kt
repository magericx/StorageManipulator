package com.magericx.storagemanipulator.ui.internal_storage.model

data class InternalStorageInfo(
    val availableStorage: Long,
    val maximumStorage:Long,
    val inUsedCapacityPercent: Double
)
