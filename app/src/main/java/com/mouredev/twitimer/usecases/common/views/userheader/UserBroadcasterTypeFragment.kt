package com.mouredev.twitimer.usecases.common.views.userheader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.UserBroadcasterTypeFragmentBinding
import com.mouredev.twitimer.model.domain.BroadcasterType
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.FontType
import com.mouredev.twitimer.util.extension.font
import com.mouredev.twitimer.util.extension.uppercaseFirst

class UserBroadcasterTypeFragment : Fragment() {

    companion object {

        private const val TYPE = "TYPE"

        @JvmStatic
        fun fragment(type: BroadcasterType) =
            UserBroadcasterTypeFragment().apply {
                arguments = Bundle().apply {
                    putString(TYPE, type.name)
                }
            }
    }

    // Properties

    private var _binding: UserBroadcasterTypeFragmentBinding? = null
    private val binding get() = _binding!!

    private var type = BroadcasterType.NONE

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UserBroadcasterTypeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Arguments
        arguments?.let { arguments ->
            arguments.getString(TYPE)?.let { sizeName ->
                type = BroadcasterType.valueOf(sizeName)
            }
        }

        // Setup
        setup()
        data()
    }

    // Private

    private fun setup() {

        context?.let { context ->
            binding.textViewType.font(FontSize.CAPTION, FontType.LIGHT, ContextCompat.getColor(context, R.color.dark))
        }
    }

    private fun data() {

        if (type != BroadcasterType.PARTNER) {
            binding.imageViewIcon.visibility = View.GONE
        }

        binding.textViewType.text = type.type.uppercaseFirst()

        if (type == BroadcasterType.NONE) {
            view?.visibility = View.GONE
        }
    }

}