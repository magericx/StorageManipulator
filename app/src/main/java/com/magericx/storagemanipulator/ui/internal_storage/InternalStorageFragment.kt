package com.magericx.storagemanipulator.ui.internal_storage

import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magericx.storagemanipulator.R
import com.magericx.storagemanipulator.databinding.FragmentInternalBinding
import com.magericx.storagemanipulator.utility.SizeUtil
import com.magericx.storagemanipulator.utility.ToastHelper.toast
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
        //to update UI according to current info for device
        internalViewModel.internalStorageInfoObserver.observe(viewLifecycleOwner, { internalInfo ->
            updateProgressComponent(
                internalInfo.inUsedCapacityPercent,
                internalInfo.availableStorage,
                internalInfo.maximumStorage
            )
        })
        //to update UI according to generation status
        internalViewModel.generateFilesInfoObserver.observe(viewLifecycleOwner,
            { generateFileInfo ->
                generateFileInfo.status?.let { status ->
                    if (status == GenerateStatus.INPROGRESS) {
                        //generating in progress, update progressbar
                        binding.titleStatusProgress.text = getString(R.string.adding)
                        generateFileInfo.let {
                            getSelectedUnit().let { unitStatus ->
                                updateProgressComponent(
                                    it.progressStatus!!.toDouble(),
                                    SizeUtil.getCapacityWithConversionToUnit(
                                        it.addProgressInfo!!.addedSize,
                                        unitStatus
                                    ),
                                    SizeUtil.getCapacityWithConversionToUnit(
                                        it.addProgressInfo.totalGenerateSize,
                                        unitStatus
                                    )
                                )
                            }

                        }
                    } else {
                        activity?.toast(status.status)
                        if (status == GenerateStatus.COMPLETED) {
                            binding.titleStatusProgress.text =
                                getString(R.string.remaining_size_status_label)
                            internalViewModel.refreshAll(getSelectedUnit())
                        }
                    }
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
                    updateToCurrentUnit(getString(R.string.kilobytes_space), UnitStatus.KB)
                }
                R.id.radio_button_mb -> {
                    updateToCurrentUnit(getString(R.string.megabytes_space), UnitStatus.MB)
                }
                R.id.radio_button_gb -> {
                    updateToCurrentUnit(getString(R.string.gigabytes_space), UnitStatus.GB)
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
                val retrievedSize = binding.inputTextSizeField.text.toString().toDouble()
                internalViewModel.generateFiles(size = retrievedSize, unit = getSelectedUnit())
            }
        }
    }

    //method to update units label
    private fun updateToCurrentUnit(unit: String, unitStatus: UnitStatus) {
        unit.let {
            binding.titleStatisticsProgressUnit.text = it
            binding.inputTextSizeContainer.suffixText = it
        }
        internalViewModel.getInternalStorageInfo(unitStatus)
    }

    //method to update progress related information
    private fun updateProgressComponent(
        progressPercent: Double,
        numerator: Double,
        denominator: Double
    ) {
        binding.progressBar.progress = progressPercent.roundToInt()
        binding.textProgress.text = getString(R.string.string_with_percent, progressPercent)
        binding.titleAvailInternalCapacity.text = numerator.toString()
        binding.titleTotalInternalCapacity.text = denominator.toString()
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