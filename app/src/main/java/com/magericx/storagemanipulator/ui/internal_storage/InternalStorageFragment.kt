package com.magericx.storagemanipulator.ui.internal_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.magericx.storagemanipulator.databinding.FragmentInternalBinding
import com.magericx.storagemanipulator.utility.ToastHelper.toast
import java.lang.IllegalArgumentException
import kotlin.math.roundToInt

class InternalStorageFragment : Fragment() {

    private val internalViewModel: InternalStorageViewModel by activityViewModels()
    private lateinit var internalStorageViewModel: InternalStorageViewModel

    private var _binding: FragmentInternalBinding? = null
    private val binding get() = _binding!!


    companion object {
        const val TAG = "InternalStorageFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        internalStorageViewModel =
            ViewModelProvider(this).get(InternalStorageViewModel::class.java)
        _binding = FragmentInternalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstScreenInfo()
        setupListeners()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setFirstScreenInfo() {
        internalViewModel.getInternalStorageInfo(getSelectedUnit())
        internalViewModel.internalStorageInfoObserver.observe(viewLifecycleOwner, { internalInfo ->
            internalInfo.inUsedCapacityPercent.let {
                binding.progressBar.progress = it.roundToInt()
                binding.textProgress.text = getString(R.string.string_with_percent, it)
                binding.titleAvailInternalCapacity.text = internalInfo.availableStorage.toString()
                binding.titleTotalInternalCapacity.text = internalInfo.maximumStorage.toString()
            }
        })
    }

    //setup listeners for internal page
    private fun setupListeners() {
        //listener for toggle
        binding.toggleMaxSize.setOnCheckedChangeListener { _, isChecked ->
            binding.inputTextSizeContainer.isEnabled = !isChecked
            binding.inputTextSizeField.isEnabled = !isChecked
        }
        binding.unitSizeRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                R.id.radio_button_kb -> {
                    getString(R.string.kilobytes_space).let {
                        binding.titleStatisticsProgressUnit.text = it
                        binding.inputTextSizeContainer.suffixText = it
                    }
                    internalViewModel.getInternalStorageInfo(UnitStatus.KB)
                }
                R.id.radio_button_mb -> {
                    getString(R.string.megabytes_space).let {
                        binding.titleStatisticsProgressUnit.text = it
                        binding.inputTextSizeContainer.suffixText = it
                    }
                    internalViewModel.getInternalStorageInfo(UnitStatus.MB)
                }
                R.id.radio_button_gb -> {
                    getString(R.string.gigabytes_space).let {
                        binding.titleStatisticsProgressUnit.text = it
                        binding.inputTextSizeContainer.suffixText = it
                    }
                    internalViewModel.getInternalStorageInfo(UnitStatus.GB)
                }

            }
        }
        binding.generateFileButton.setOnClickListener {
            if (binding.inputTextSizeField.text.isNullOrEmpty() && binding.inputTextSizeContainer.isEnabled) {
                activity?.toast(getString(R.string.file_size_error_message))
                return@setOnClickListener
            }
            if (!binding.inputTextSizeContainer.isEnabled) {
                internalViewModel.generateFiles(max = true)
            } else {
                //generate files based on inputted file size
            }
        }
    }

    private fun getSelectedUnit(): UnitStatus {
        return when (binding.unitSizeRadioGroup.checkedRadioButtonId) {
            R.id.radio_button_kb -> UnitStatus.KB
            R.id.radio_button_mb -> UnitStatus.MB
            R.id.radio_button_gb -> UnitStatus.GB
            else -> throw IllegalArgumentException("Unsupported unit type")
        }
    }
}