package com.magericx.storagemanipulator.ui.internal_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.magericx.storagemanipulator.R
import kotlin.math.roundToInt

class InternalStorageFragment : Fragment() {

    private val internalViewModel: InternalStorageViewModel by activityViewModels()
    private lateinit var internalStorageViewModel: InternalStorageViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var textProgress: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        internalStorageViewModel =
            ViewModelProvider(this).get(InternalStorageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_internal, container, false)
        progressBar = root.findViewById(R.id.progressBar)
        textProgress = root.findViewById(R.id.text_progress)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstScreenInfo()
    }

    private fun setFirstScreenInfo() {
        internalViewModel.setFirstScreenInfo()
        internalViewModel.internalStorageInfoObserver.observe(viewLifecycleOwner, { internalInfo ->
            internalInfo.inUsedCapacityPercent.let {
                progressBar.progress = it.roundToInt()
                textProgress.text = getString(R.string.string_with_percent,it)
            }
        })
    }
}