package com.magericx.storagemanipulator.ui.internal_storage.model

import com.magericx.storagemanipulator.ui.internal_storage.DeleteStatus
import com.magericx.storagemanipulator.ui.internal_storage.GenerateStatus

data class StorageInfo(
    val availableStorage: Double,
    val maximumStorage: Double,
    val inUsedCapacityPercent: Double
)

data class GenerateFilesInfo(
    val status: GenerateStatus? = null,
    val progressStatus: Double? = null,
    val addProgressInfo: AddProgressInfo? = null
)

data class AddProgressInfo(
    val addedSize: Long = 0,
    val totalGenerateSize: Long
)