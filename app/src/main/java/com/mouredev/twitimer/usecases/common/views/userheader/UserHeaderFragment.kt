package com.mouredev.twitimer.usecases.common.views.userheader

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.UserHeaderFragmentBinding
import com.mouredev.twitimer.model.domain.BroadcasterType
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.usecases.common.controls.ChannelButtonFragment
import com.mouredev.twitimer.util.*
import com.mouredev.twitimer.util.extension.font
import com.mouredev.twitimer.util.extension.removeSocialInvalidCharacters

class UserHeaderFragment : Fragment() {

    companion object {

        private const val USER = "USER"
        private const val SMALL = "SMALL"
        private const val READ_ONLY = "READ_ONLY"

        @JvmStatic
        fun fragment(user: User? = null, readOnly: Boolean/*, small: Boolean = false*/) =
            UserHeaderFragment().apply {
                arguments = Bundle().apply {
                    user?.let { user ->
                        putString(USER, User.toJson(user))
                    }
                    putBoolean(READ_ONLY, readOnly)
                    putBoolean(SMALL, false) // No soportado
                }
            }
    }

    // Properties

    private var _binding: UserHeaderFragmentBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null
    private var readOnly = true
    private var small = false

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UserHeaderFragmentBinding.inflate(inflater, container, false)
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
            arguments.getString(USER)?.let { userJSON ->
                user = User.fromJson(userJSON)
            }
            readOnly = arguments.getBoolean(READ_ONLY)
            small = arguments.getBoolean(SMALL)

            // Setup
            setup()
            data()
        }
    }

    // Private

    private fun setup() {

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutUserAvatar, UserAvatarFragment.fragment(user?.profileImageUrl ?: "", user?.login ?: "", if (small) Size.VERY_BIG else Size.GIGANT, !readOnly))
        transaction?.replace(R.id.frameLayoutBroadcasterType, UserBroadcasterTypeFragment.fragment(user?.broadcasterType ?: BroadcasterType.NONE))
        transaction?.replace(R.id.frameLayoutButtonChannel, ChannelButtonFragment.fragment(user?.login ?: "", false))
        transaction?.disallowAddToBackStack()
        transaction?.commit()

        context?.let { context ->

            if (small) {
                binding.textViewUser.font(FontSize.BUTTON, FontType.BOLD, color = ContextCompat.getColor(context, R.color.light))
                binding.textViewUser.maxLines = 2
                binding.textViewUsername.visibility = View.GONE
            } else {
                binding.textViewUser.font(FontSize.SUBTITLE, FontType.BOLD, color = ContextCompat.getColor(context, R.color.light))
                binding.textViewUsername.font(FontSize.BODY, FontType.LIGHT, ContextCompat.getColor(context, R.color.light))
            }
        }

        setupSocialButtons()
    }

    private fun setupSocialButtons() {

        binding.imageButtonDiscord.visibility = View.GONE
        binding.imageButtonYouTube.visibility = View.GONE
        binding.imageButtonTwitter.visibility = View.GONE
        binding.imageButtonInstagram.visibility = View.GONE
        binding.imageButtonTikTok.visibility = View.GONE

        if (user?.streamer == true) {

            context?.let { context ->
                user?.settings?.let { settings ->
                    if (settings.discord?.isNotBlank() == true) {
                        binding.imageButtonDiscord.visibility = View.VISIBLE
                        binding.imageButtonDiscord.setOnClickListener {
                            Util.openBrowser(context, "${Constants.DISCORD_URI}${settings.discord?.removeSocialInvalidCharacters()}")
                        }
                    }
                    if (settings.youtube?.isNotBlank() == true) {
                        binding.imageButtonYouTube.visibility = View.VISIBLE
                        binding.imageButtonYouTube.setOnClickListener {
                            Util.openBrowser(context, "${Constants.YOUTUBE_URI}${settings.youtube?.removeSocialInvalidCharacters()}")
                        }
                    }
                    if (settings.twitter?.isNotBlank() == true) {
                        binding.imageButtonTwitter.visibility = View.VISIBLE
                        binding.imageButtonTwitter.setOnClickListener {
                            Util.openBrowser(context, "${Constants.TWITTER_URI}${settings.twitter?.removeSocialInvalidCharacters()}")
                        }
                    }
                    if (settings.instagram?.isNotBlank() == true) {
                        binding.imageButtonInstagram.visibility = View.VISIBLE
                        binding.imageButtonInstagram.setOnClickListener {
                            Util.openBrowser(context, "${Constants.INSTAGRAM_URI}${settings.instagram?.removeSocialInvalidCharacters()}")
                        }
                    }
                    if (settings.tiktok?.isNotBlank() == true) {
                        binding.imageButtonTikTok.visibility = View.VISIBLE
                        binding.imageButtonTikTok.setOnClickListener {
                            // Pasamos el user a minus ya que la App de TikTok es case sensitive
                            Util.openBrowser(context, "${Constants.TIKTOK_URI}${settings.tiktok?.lowercase()?.removeSocialInvalidCharacters()}")
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun data() {

        binding.textViewUser.text = user?.displayName ?: ""
        binding.textViewUsername.text = "@${user?.login ?: ""}"
    }

}