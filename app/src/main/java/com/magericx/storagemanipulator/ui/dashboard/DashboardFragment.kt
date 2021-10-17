package com.magericx.storagemanipulator.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magericx.storagemanipulator.R

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var deviceName: TextView
    private lateinit var manufacturer: TextView
    private lateinit var operatingVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        deviceName = root.findViewById(R.id.text_device_name)
        manufacturer = root.findViewById(R.id.text_manufacturer_name)
        operatingVersion = root.findViewById(R.id.text_operating_system)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstScreenInfo()
    }

    private fun setFirstScreenInfo() {
        dashboardViewModel.setFirstScreenInfo()
        dashboardViewModel.deviceInfoObserver.observe(viewLifecycleOwner, { deviceInfo ->
            deviceName.text = deviceInfo.deviceName
            manufacturer.text = deviceInfo.manufacturer
            operatingVersion.text = deviceInfo.operatingVersion
        })
    }


}