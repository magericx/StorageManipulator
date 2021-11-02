package com.magericx.storagemanipulator.handler

object ProgressHandler {

    @Volatile
    var internalPause = false

    fun updateInternalPause(){
        internalPause = !internalPause
    }
}