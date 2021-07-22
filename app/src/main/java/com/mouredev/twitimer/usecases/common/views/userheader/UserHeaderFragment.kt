package com.mouredev.twitimer.usecases.common.views.userheader

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.UserHeaderFragmentBinding
import com.mouredev.twitimer.model.domain.BroadcasterType
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.usecases.common.controls.ChannelButtonFragment
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.FontType
import com.mouredev.twitimer.util.Size
import com.mouredev.twitimer.util.extension.font

class UserHeaderFragment : Fragment() {

    companion object {

        private const val USER = "USER"
        private const val SMALL = "SMALL"

        @JvmStatic
        fun fragment(user: User? = null, small: Boolean = false) =
            UserHeaderFragment().apply {
                arguments = Bundle().apply {
                    user?.let { user ->
                        putString(USER, User.toJson(user))
                    }
                    putBoolean(SMALL, small)
                }
            }
    }

    // Properties

    private var _binding: UserHeaderFragmentBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null
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
            small = arguments.getBoolean(SMALL)

            // Setup
            setup()
            data()
        }
    }

    // Private

    private fun setup() {

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutUserAvatar, UserAvatarFragment.fragment(user?.profileImageUrl ?: "", user?.login ?: "", if (small) Size.VERY_BIG else Size.GIGANT))
        transaction?.replace(R.id.frameLayoutBroadcasterType, UserBroadcasterTypeFragment.fragment(user?.broadcasterType ?: BroadcasterType.NONE))
        transaction?.replace(if (small) R.id.frameLayoutButtonChannelSmall else R.id.frameLayoutButtonChannel, ChannelButtonFragment.fragment(user?.login ?: "", false))
        transaction?.disallowAddToBackStack()
        transaction?.commit()

        context?.let { context ->

            if (small) {
                binding.textViewUser.font(FontSize.HEAD, color = ContextCompat.getColor(context, R.color.light))
                binding.textViewUsername.visibility = View.GONE
            } else {
                binding.textViewUser.font(FontSize.TITLE, color = ContextCompat.getColor(context, R.color.light))
                binding.textViewUsername.font(FontSize.BODY, FontType.LIGHT, ContextCompat.getColor(context, R.color.light))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun data() {

        binding.textViewUser.text = user?.displayName ?: ""
        binding.textViewUsername.text = "@${user?.login ?: ""}"
    }

}