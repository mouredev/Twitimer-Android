package com.mouredev.twitimer.usecases.common.controls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mouredev.twitimer.databinding.ProgressBarFragmentBinding

class ProgressBarFragment : Fragment() {

    // Properties

    private var _binding: ProgressBarFragmentBinding? = null
    private val binding get() = _binding!!

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProgressBarFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() = ProgressBarFragment()
    }

}