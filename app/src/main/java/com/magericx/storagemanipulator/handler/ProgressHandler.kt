package com.magericx.storagemanipulator.handler

object ProgressHandler {

    @Volatile
    var internalPause = false
    var externalPause = false


    fun updateInternalPause(){
        internalPause = !internalPause
    }

    fun resetExternalPauseStatus(){
        externalPause = false
    }

    fun updateExternalPause(){
        externalPause = !externalPause
    }

    fun resetInternalPauseStatus(){
        internalPause = false
    }


}