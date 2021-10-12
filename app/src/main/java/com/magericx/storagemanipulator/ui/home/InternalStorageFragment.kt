package com.magericx.storagemanipulator.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magericx.storagemanipulator.R

class InternalStorageFragment : Fragment() {

    private lateinit var internalStorageViewModel: InternalStorageViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        internalStorageViewModel =
                ViewModelProvider(this).get(InternalStorageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_internal, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        internalStorageViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}