package com.magericx.storagemanipulator.ui.external_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magericx.storagemanipulator.R
import com.magericx.storagemanipulator.databinding.FragmentExternalBinding
import com.magericx.storagemanipulator.databinding.FragmentInternalBinding
import com.magericx.storagemanipulator.ui.dashboard.DashboardFragment
import com.magericx.storagemanipulator.ui.internal_storage.InternalStorageFragment
import com.magericx.storagemanipulator.ui.internal_storage.InternalStorageViewModel

class ExternalStorageFragment : Fragment() {

    private val externalViewModel: ExternalStorageViewModel by activityViewModels()


    private var _binding: FragmentExternalBinding? = null
    private val binding get() = _binding!!.rootView

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
    ): View {
        _binding = FragmentExternalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstScreenInfo()
        setupListeners()
    }

    private fun setupListeners() {
        TODO("Not yet implemented")
    }

    private fun setFirstScreenInfo() {
        TODO("Not yet implemented")
    }

}