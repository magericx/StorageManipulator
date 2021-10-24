package com.magericx.storagemanipulator.ui.internal_storage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.magericx.storagemanipulator.R
import kotlin.math.roundToInt

class InternalStorageFragment : Fragment() {

    private val internalViewModel: InternalStorageViewModel by activityViewModels()
    private lateinit var internalStorageViewModel: InternalStorageViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var textProgress: TextView
    private lateinit var maxSizeToggle: SwitchMaterial
    private lateinit var textSizeInputContainer: TextInputLayout
    private lateinit var textSizeInputField: TextInputEditText
    private lateinit var unitSizeRadioGroup: RadioGroup
    private lateinit var unitStatistics: TextView

    companion object {
        const val TAG = "InternalStorageFragment"
    }

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
        maxSizeToggle = root.findViewById(R.id.toggle_max_size)
        textSizeInputContainer = root.findViewById(R.id.input_textSize_container)
        textSizeInputField = root.findViewById(R.id.input_textSize_field)
        unitSizeRadioGroup = root.findViewById(R.id.unit_size_radioGroup)
        unitStatistics = root.findViewById(R.id.title_statistics_progress_unit)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstScreenInfo()
        setupListeners()
    }

    private fun setFirstScreenInfo() {
        internalViewModel.setFirstScreenInfo()
        internalViewModel.internalStorageInfoObserver.observe(viewLifecycleOwner, { internalInfo ->
            internalInfo.inUsedCapacityPercent.let {
                progressBar.progress = it.roundToInt()
                textProgress.text = getString(R.string.string_with_percent, it)
            }
        })
    }

    //setup listeners for internal page
    private fun setupListeners() {
        //listener for toggle
        maxSizeToggle.setOnCheckedChangeListener { _, isChecked ->
            textSizeInputContainer.isEnabled = !isChecked
            textSizeInputField.isEnabled = !isChecked
        }
        unitSizeRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            unitStatistics.text.apply{
                when (checkedId) {
                    R.id.radio_button_kb -> {
                        //"text"
                        R.string.kilobytes_space
                        Log.d(TAG, "Set kb here")
                    }
                    R.id.radio_button_mb -> {
                        R.string.megabytes_space
                        Log.d(TAG, "Set mb here")
                    }
                    R.id.radio_button_gb -> {
                        R.string.gigabytes_space
                        Log.d(TAG, "Set gb here")
                    }
                }
            }
        }
    }
}