package com.magericx.storagemanipulator

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import java.lang.ref.WeakReference

class NavControllerListener(private val listener: WeakReference<MainActivity.TabListener>) : NavController.OnDestinationChangedListener {
    private val weakListener = listener.get()
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.label) {
            NavBarHeader.EXTERNAL_STORAGE.navBarName -> {
                weakListener?.updateResult()
            }
            else -> {
                //do nothing here
            }
        }
    }
}

