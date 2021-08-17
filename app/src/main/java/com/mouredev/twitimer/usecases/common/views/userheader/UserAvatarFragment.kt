package com.mouredev.twitimer.usecases.common.views.userheader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.UserAvatarFragmentBinding
import com.mouredev.twitimer.usecases.settings.SettingsRouter
import com.mouredev.twitimer.util.Size
import com.mouredev.twitimer.util.UIUtil
import com.mouredev.twitimer.util.Util

class UserAvatarFragment : Fragment() {

    companion object {

        private const val URL = "URL"
        private const val USER = "USER"
        private const val SIZE = "SIZE"
        private const val SETTINGS = "SETTINGS"

        @JvmStatic
        fun fragment(url: String, user: String, size: Size, settings: Boolean) =
            UserAvatarFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, url)
                    putString(USER, user)
                    putString(SIZE, size.name)
                    putBoolean(SETTINGS, settings)
                }
            }
    }

    // Properties

    private var _binding: UserAvatarFragmentBinding? = null
    private val binding get() = _binding!!

    private var url: String? = null
    private var user: String? = null
    private var size = Size.GIGANT
    private var settings = false

    // Initialization

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = UserAvatarFragmentBinding.inflate(inflater, container, false)
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
            url = arguments.getString(URL)
            user = arguments.getString(USER)
            arguments.getString(SIZE)?.let { sizeName ->
                size = Size.valueOf(sizeName)
            }
            settings = arguments.getBoolean(SETTINGS)

            // Setup
            setup()
            data()

        }
    }

    // Private

    private fun setup() {

        context?.let { context ->
            val dp = Util.dpToPixel(context, size.size).toInt()
            binding.imageViewAvatar.layoutParams.width = dp
            binding.imageViewAvatar.layoutParams.height = dp

            if(settings) {
                binding.imageButtonSettings.setOnClickListener {
                    SettingsRouter().launch(context)
                }
            } else {
                binding.imageButtonSettings.visibility = View.GONE
            }
        }
    }

    private fun data() {
       UIUtil.loadAvatar(context, url, user, binding.imageViewAvatar)
    }

}