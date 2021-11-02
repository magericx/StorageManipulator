package com.magericx.storagemanipulator.ui.external_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magericx.storagemanipulator.R
import com.magericx.storagemanipulator.ui.dashboard.DashboardFragment
import com.magericx.storagemanipulator.ui.internal_storage.InternalStorageFragment

class ExternalStorageFragment : Fragment() {

    private lateinit var externalStorageViewModel: ExternalStorageViewModel

    companion object {
        const val TAG = "ExternalStorageFragment"
        fun getInstance(): Fragment {
            val bundle = Bundle()
            val tabFragment = ExternalStorageFragment()
            tabFragment.arguments = bundle
            return tabFragment
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        externalStorageViewModel =
                ViewModelProvider(this).get(ExternalStorageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_external, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        externalStorageViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}