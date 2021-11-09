package com.magericx.storagemanipulator.ui.external_storage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.magericx.storagemanipulator.R
import com.magericx.storagemanipulator.databinding.FragmentExternalBinding
import com.magericx.storagemanipulator.ui.internal_storage.DeleteStatus
import com.magericx.storagemanipulator.ui.internal_storage.GenerateStatus
import com.magericx.storagemanipulator.ui.internal_storage.UnitStatus
import com.magericx.storagemanipulator.utility.SizeUtil
import com.magericx.storagemanipulator.utility.ToastHelper.toast
import kotlin.math.roundToInt

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
        checkExternalAvailable()
    }

    override fun onResume(){
        super.onResume()
        checkExternalAvailable()
    }

    private fun setupListeners() {
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
                externalViewModel.generateFiles(max = true)
            } else {
                val retrievedSize = binding.inputTextSizeField.text.toString().toDouble()
                externalViewModel.generateFiles(size = retrievedSize, unit = getSelectedUnit())
            }
        }

        binding.deleteFileButton.setOnClickListener {
            externalViewModel.deleteFiles()
        }

        binding.cancelButton.setOnClickListener {
            externalViewModel.cancelGenerate()
        }

        binding.statusButton.setOnClickListener {
            val previousTag = getGenerationButtonState()
            when (previousTag) {
                true -> {
                    externalViewModel.pauseGenerate()
                    //do pause here
                }
                false -> {
                    externalViewModel.pauseGenerate()
                    //do resume here
                }
            }
            previousTag.not().let {
                binding.statusButton.setTag(R.string.button_status_tag, it)
                binding.statusButton.text =
                    if (it) getString(R.string.pause) else getString(R.string.resume)
                val resourceImage = if (it) {
                    ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.ic_media_pause,
                        null
                    )
                } else {
                    ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.ic_media_play,
                        null
                    )
                }
                (binding.statusButton as MaterialButton).icon = resourceImage
            }
        }
    }

    private fun setFirstScreenInfo() {
        externalViewModel.getExternalStorageInfo(getSelectedUnit())
        //to update UI according to current info for device
        externalViewModel.externalStorageInfoObserver.observe(viewLifecycleOwner, { internalInfo ->
            updateProgressComponent(
                internalInfo.inUsedCapacityPercent,
                internalInfo.availableStorage,
                internalInfo.maximumStorage
            )
        })

        //to update UI according to generation status
        externalViewModel.generateFilesInfoObserver.observe(viewLifecycleOwner,
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
                        setStatusButtonVisible()
                    } else {
                        activity?.toast(status.status)
                        if (status == GenerateStatus.COMPLETED || status == GenerateStatus.CANCELLED) {
                            binding.titleStatusProgress.text =
                                getString(R.string.remaining_size_status_label)
                            externalViewModel.refreshAll(getSelectedUnit())
                            setStatusButtonInvisible()
                        }
                    }
                }
            })

        externalViewModel.deleteFilesInfoObserver.observe(viewLifecycleOwner,
            { deleteStatus ->
                deleteStatus?.let {
                    activity?.toast(it.status)
                    if (it == DeleteStatus.CONFLICT) return@observe
                    externalViewModel.refreshAll(getSelectedUnit())
                }
            })
    }

    //During generation
    private fun setStatusButtonVisible() {
        if (binding.statusButton.visibility == View.INVISIBLE) {
            binding.statusButton.visibility = View.VISIBLE
        }
        if (binding.cancelButton.visibility == View.INVISIBLE) {
            binding.cancelButton.visibility = View.VISIBLE
        }
        binding.statusButton.getTag(R.string.button_status_tag).let {
            if (it == null) {
                binding.statusButton.setTag(R.string.button_status_tag, true)
            }
        }

    }

    //After generation
    private fun setStatusButtonInvisible() {
        if (binding.statusButton.visibility == View.VISIBLE) {
            binding.statusButton.visibility = View.INVISIBLE
        }
        if (binding.cancelButton.visibility == View.VISIBLE) {
            binding.cancelButton.visibility = View.INVISIBLE
        }
        //if button is false = resume state, we need to reset to prepare for next cycle
        binding.statusButton.let { btn ->
            if (!(btn.getTag(R.string.button_status_tag) as Boolean)) {
                (btn as MaterialButton).apply {
                    setTag(R.string.button_status_tag, null)
                    text = getString(R.string.pause)
                    icon = ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.ic_media_pause,
                        null
                    )
                }
            }
        }
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


    private fun getGenerationButtonState(): Boolean {
        return (binding.statusButton.getTag(R.string.button_status_tag) ?: true) as Boolean
    }

    //method to update units label
    private fun updateToCurrentUnit(unit: String, unitStatus: UnitStatus) {
        unit.let {
            binding.titleStatisticsProgressUnit.text = it
            binding.inputTextSizeContainer.suffixText = it
        }
        externalViewModel.getExternalStorageInfo(unitStatus)
    }

    private fun checkExternalAvailable(){
        if (!externalViewModel.checkExternalAvailable()){
            binding.let{ root->
                root.generateFileButton.isEnabled = false
                root.deleteFileButton.isEnabled = false
                getString(R.string.text_unknown).let{
                    root.textProgress.text = it
                    root.titleAvailInternalCapacity.text = it
                }
                root.titleTotalInternalCapacity.visibility = View.INVISIBLE
                root.titleDivider.visibility = View.INVISIBLE
            }
        }
    }


}