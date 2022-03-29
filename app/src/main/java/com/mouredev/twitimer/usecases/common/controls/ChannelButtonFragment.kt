package com.mouredev.twitimer.usecases.common.controls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ChannelButtonFragmentBinding
import com.mouredev.twitimer.util.Constants
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.FontType
import com.mouredev.twitimer.util.Util
import com.mouredev.twitimer.util.extension.font

class ChannelButtonFragment : Fragment() {

    companion object {

        private const val LOGIN = "LOGIN"
        private const val DARK_BACKGROUND = "DARK_BACKGROUND"

        @JvmStatic
        fun fragment(login: String, darkBackground: Boolean) =
            ChannelButtonFragment().apply {
                arguments = Bundle().apply {
                    putString(LOGIN, login)
                    putBoolean(DARK_BACKGROUND, darkBackground)
                }
            }
    }

    // Properties

    private var _binding: ChannelButtonFragmentBinding? = null
    private val binding get() = _binding!!

    private var login: String? = null
    private var darkBackground: Boolean? = null

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ChannelButtonFragmentBinding.inflate(inflater, container, false)
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
            login = arguments.getString(LOGIN)
            darkBackground = arguments.getBoolean(DARK_BACKGROUND)
        }

        // Setup
        setup()
    }

    // Private

    private fun setup() {

        context?.let { context ->
            binding.buttonChannel.background = ContextCompat.getDrawable(context, if (darkBackground == true) R.drawable.channel_button_round_dark else R.drawable.channel_button_round)

            binding.buttonChannel.setOnClickListener {
                login?.let { login ->
                    val url = "${Constants.TWITCH_PROFILE_URI}${login}"
                    Util.openBrowser(context, url)
                }
            }
        }
    }

}