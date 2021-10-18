package com.magericx.storagemanipulator.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magericx.storagemanipulator.R

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var deviceName: TextView
    private lateinit var manufacturer: TextView
    private lateinit var operatingVersion: TextView
    private lateinit var totalMemory: TextView
    private lateinit var availMemory: TextView
    private lateinit var releaseVersion: TextView

    companion object {
        const val TAG = "DashboardFragment"
    }

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
        totalMemory = root.findViewById(R.id.text_total_device_memory)
        availMemory = root.findViewById(R.id.text_avail_device_memory)
        releaseVersion = root.findViewById(R.id.text_version_release)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstScreenInfo()
    }

    private fun setFirstScreenInfo() {
        Log.d(TAG, "Called setFirstScreenInfo here")
        dashboardViewModel.setFirstScreenInfo()
        dashboardViewModel.deviceInfoObserver.observe(viewLifecycleOwner, { deviceInfo ->
            Log.d(TAG, "Received observer here")
            deviceName.text = deviceInfo.deviceName
            manufacturer.text = deviceInfo.manufacturer
            operatingVersion.text = deviceInfo.operatingVersion
            totalMemory.text = if (deviceInfo.totalMemory != 0.toLong()) deviceInfo.totalMemory.toString()
            else InformationStatus.UNKNOWN.status
            availMemory.text = if (deviceInfo.availMemory != 0.toLong()) deviceInfo.availMemory.toString()
            else InformationStatus.UNKNOWN.status
            releaseVersion.text = deviceInfo.releaseVersion
        })

    }

}

enum class InformationStatus(val status: String) {
    UNKNOWN("Unknown"), RETRIEVING("Retrieving")
}