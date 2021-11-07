package com.magericx.storagemanipulator.handler

class InternalProgressManager : ProgressManager {

    override fun getProgressStatus(): Boolean {
        return ProgressHandler.internalPause
    }
}

class ExternalProgressManager : ProgressManager {
    override fun getProgressStatus(): Boolean {
        return ProgressHandler.externalPause
    }
}

interface ProgressManager {
    fun getProgressStatus(): Boolean
}