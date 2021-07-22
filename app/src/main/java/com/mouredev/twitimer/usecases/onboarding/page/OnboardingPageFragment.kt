package com.mouredev.twitimer.usecases.onboarding.page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.OnboardingPageFragmentBinding
import com.mouredev.twitimer.model.domain.Onboarding
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.FontType
import com.mouredev.twitimer.util.extension.font

class OnboardingPageFragment : Fragment() {

    companion object {

        private const val PAGE = "PAGE"

        @JvmStatic
        fun fragment(page: Onboarding) =
            OnboardingPageFragment().apply {
                arguments = Bundle().apply {
                    putString(PAGE, Gson().toJson(page))
                }
            }
    }

    // Properties

    private var _binding: OnboardingPageFragmentBinding? = null
    private val binding get() = _binding!!

    private var page: Onboarding? = null

    // Initialization

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = OnboardingPageFragmentBinding.inflate(inflater, container, false)
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
            page = Gson().fromJson(arguments.getString(PAGE), Onboarding::class.java)
        }

        // Setup
        page?.let { page ->
            setup(page)
        }

    }

    // Private

    private fun setup(page: Onboarding) {

        context?.let { context ->

            // UI
            binding.textViewOnboardingTitle.font(FontSize.SUBTITLE, FontType.BOLD, context.getColor(R.color.light))
            binding.textViewOnboardingBody.font(FontSize.SUBHEAD, FontType.LIGHT, context.getColor(R.color.light))

            // Data
            binding.imageViewOnboarding.setImageResource(page.image)
            binding.textViewOnboardingTitle.text = context.getString(page.title)
            binding.textViewOnboardingBody.text = context.getString(page.body)
        }

    }

}