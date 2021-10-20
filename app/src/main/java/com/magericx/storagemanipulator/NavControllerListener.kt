package com.magericx.storagemanipulator

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination

class NavControllerListener(private val listener: MainActivity.TabListener) : NavController.OnDestinationChangedListener {
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.label) {
            NavBarHeader.EXTERNAL_STORAGE.navBarName -> {
                listener.updateResult()
            }
            else -> {
                //do nothing here
            }
        }
    }
}

