package com.mouredev.twitimer.usecases.common.views.info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.InfoFragmentBinding
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.FontType
import com.mouredev.twitimer.util.extension.center
import com.mouredev.twitimer.util.extension.font
import com.mouredev.twitimer.util.extension.primary

class InfoFragment : Fragment() {

    companion object {
        fun fragment() = InfoFragment()
    }

    // Properties

    private var _binding: InfoFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: InfoViewModel
    private var listener: InfoFragmentListener? = null

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = InfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Model
        viewModel = ViewModelProvider(this).get(InfoViewModel::class.java)

        // Arguments
        arguments?.let { arguments ->
            InfoRouter.type(arguments)?.let { type ->
                viewModel.type = type
            }
            viewModel.extra = InfoRouter.extra(arguments)

            // Setup
            setup()
            data()
        }
    }

    // Public

    fun setListener(listener: InfoFragmentListener) {
        this.listener = listener
    }

    // Private

    private fun setup() {

        // UI

        binding.textViewTitle.font(FontSize.SUBTITLE, FontType.BOLD)
        binding.textViewTitle.center()
        binding.textViewBody.font(FontSize.BUTTON, FontType.LIGHT)
        binding.textViewBody.center()

        binding.textViewFooterFirst.font(FontSize.CAPTION, FontType.LIGHT)
        binding.textViewFooterSecond.font(FontSize.CAPTION, FontType.LIGHT)

        binding.buttonAction.primary {
            viewModel.action(listener)
        }

        context?.let { context ->

            binding.imageButtonTwitter.setOnClickListener {
                viewModel.tweet(context)
            }

            binding.imageButtonShare.setOnClickListener {
                viewModel.share(context)
            }

            if (viewModel.type == InfoViewType.AUTH) {
                view?.setBackgroundColor(ContextCompat.getColor(context, R.color.background))
            } else {
                view?.setBackgroundColor(ContextCompat.getColor(context, R.color.secondary_background))
            }
        }

        binding.linearLayoutShare.visibility = View.GONE
    }

    private fun data() {

        context?.let { context ->
            binding.imageViewIcon.setImageDrawable(ContextCompat.getDrawable(context, viewModel.image))
        }

        binding.textViewTitle.text = getString(viewModel.title)
        binding.textViewBody.text = getString(viewModel.body)
        binding.buttonAction.text = getString(viewModel.advice(1))

        when (viewModel.type) {
            InfoViewType.COUNTDOWN ->
                binding.linearLayoutFooter.visibility = View.GONE
            InfoViewType.SEARCH -> {
                binding.buttonAction.visibility = View.GONE
                binding.textViewFooterFirst.text = getString(viewModel.advice(1))
                binding.textViewFooterSecond.text = getString(viewModel.advice(2))
                context?.let { context ->
                    viewModel.icon(1)?.let { icon ->
                        binding.imageViewFooterFirst.setImageDrawable(ContextCompat.getDrawable(context, icon))

                    }
                    viewModel.icon(2)?.let { icon ->
                        binding.imageViewFooterSecond.setImageDrawable(ContextCompat.getDrawable(context, icon))
                    }
                }
            }
            InfoViewType.CHANNEL, InfoViewType.STREAMER, InfoViewType.SCHEDULE -> {
                binding.buttonAction.visibility = View.GONE
                binding.textViewFooterFirst.text = getString(viewModel.advice(1))
                context?.let { context ->
                    viewModel.icon(1)?.let { icon ->
                        binding.imageViewFooterFirst.setImageDrawable(ContextCompat.getDrawable(context, icon))
                    }
                    binding.linearLayoutFooterSecond.visibility = View.GONE
                }

                if (viewModel.type == InfoViewType.CHANNEL) {
                    binding.linearLayoutShare.visibility = View.VISIBLE
                    binding.textViewBody.text = getString(viewModel.body, viewModel.extra)
                }
            }
            InfoViewType.AUTH ->
                binding.linearLayoutFooter.visibility = View.GONE

            InfoViewType.HOLIDAY, InfoViewType.USER_HOLIDAYS -> {
                binding.buttonAction.visibility = View.GONE
                binding.textViewFooterFirst.text = getString(viewModel.advice(1))
                context?.let { context ->
                    viewModel.icon(1)?.let { icon ->
                        binding.imageViewFooterFirst.setImageDrawable(ContextCompat.getDrawable(context, icon))
                    }
                    binding.linearLayoutFooterSecond.visibility = View.GONE
                }
            }
        }
    }

}