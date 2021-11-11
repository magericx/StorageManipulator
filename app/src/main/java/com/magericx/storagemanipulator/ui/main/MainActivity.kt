package com.magericx.storagemanipulator.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magericx.storagemanipulator.R
import com.magericx.storagemanipulator.ui.dashboard.DashboardFragment
import com.magericx.storagemanipulator.ui.dashboard.DashboardViewModel
import com.magericx.storagemanipulator.ui.external_storage.ExternalStorageFragment
import com.magericx.storagemanipulator.ui.internal_storage.InternalStorageFragment
import com.magericx.storagemanipulator.ui.viewpager.ViewPagerAdapter
import com.magericx.storagemanipulator.utility.ToastHelper.toast
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private var navListener: OnPageChangeCallback? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var viewPager2: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    private var tabListener: TabListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager2 = findViewById<View>(R.id.viewpager2) as ViewPager2
        bottomNavigationView = findViewById<View>(R.id.nav_view) as BottomNavigationView

        addListeners()
        setupViewPager(viewPager2)

    }

    private fun setupViewPager(viewPager2: ViewPager2) {
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(DashboardFragment.getInstance())
        adapter.addFragment(InternalStorageFragment.getInstance())
        adapter.addFragment(ExternalStorageFragment.getInstance())
        viewPager2.adapter = adapter
    }

    override fun onDestroy() {
        tabListener = null
        navListener = null
        super.onDestroy()
    }

    private fun addListeners() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> viewPager2.setCurrentItem(0, false)
                R.id.internal_storage -> viewPager2.setCurrentItem(1, false)
                R.id.external_storage -> {
                    if (tabListener == null) {
                        tabListener = TabListener()
                    }
                    if (tabListener!!.checkStorageAvailable()) {
                        viewPager2.setCurrentItem(2, false)
                    }
                }
            }
            false
        }

        val bottomNavigationView = WeakReference(bottomNavigationView)
        navListener = ViewPagerListener(bottomNavigationView)
        navListener?.let {
            viewPager2.registerOnPageChangeCallback(it)
        }
    }

    inner class TabListener : ExternalStorageStatus {
        override fun checkStorageAvailable(): Boolean {
            dashboardViewModel.checkExternalAvailable().let {
                if (!it) {
                    this@MainActivity.toast(getString(R.string.external_not_mounted))
                }
                return it
            }

        }
    }
}

interface ExternalStorageStatus {
    fun checkStorageAvailable(): Boolean
}