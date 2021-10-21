package com.magericx.storagemanipulator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magericx.storagemanipulator.ui.dashboard.DashboardViewModel
import com.magericx.storagemanipulator.utility.ToastHelper.toast
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var tabListener: WeakReference<TabListener>? = null
    private var navListener: NavController.OnDestinationChangedListener? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.internal_storage, R.id.external_storage
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        addListeners()
    }

    override fun onPause() {
        removeListeners()
        super.onPause()
    }

    override fun onDestroy() {
        tabListener = null
        navListener = null
        super.onDestroy()
    }

    private fun addListeners() {
        tabListener = WeakReference(TabListener())
        navListener = NavControllerListener(tabListener!!)
        navListener?.let {
            navController.addOnDestinationChangedListener(it)
        }
    }

    private fun removeListeners() {
        navListener?.let {
            navController.removeOnDestinationChangedListener(it)
        }
    }

    inner class TabListener : NavBarStatus {
        override fun updateResult() {
            val externalStorageStatus = dashboardViewModel.checkExternalAvailable()
            if (!externalStorageStatus) {
                this@MainActivity.toast(getString(R.string.external_not_mounted))
            }
        }
    }
}

enum class NavBarHeader(val navBarName: String) {
    DASHBOARD("Dashboard"), INTERNAL_STORAGE("Internal storage"),
    EXTERNAL_STORAGE("External storage")
}

interface NavBarStatus {
    fun updateResult()
}