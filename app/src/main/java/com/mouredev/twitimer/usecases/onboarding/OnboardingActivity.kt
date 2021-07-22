package com.mouredev.twitimer.usecases.onboarding

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ActivityOnboardingBinding
import com.mouredev.twitimer.provider.preferences.PreferencesKey
import com.mouredev.twitimer.provider.preferences.PreferencesProvider
import com.mouredev.twitimer.usecases.onboarding.page.OnboardingPageAdapter
import com.mouredev.twitimer.util.extension.primary
import com.mouredev.twitimer.util.extension.secondary

class OnboardingActivity : AppCompatActivity() {

    // Properties

    private lateinit var binding: ActivityOnboardingBinding

    private lateinit var viewModel: OnboardingViewModel

    private var selection = 0
    private var dots: Array<TextView?> = arrayOfNulls(0)

    // Initialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)


        // Content
        setContentView(binding.root)

        supportActionBar?.hide()

        // View Model
        viewModel = ViewModelProvider(this).get(OnboardingViewModel::class.java)

        // Setup
        localize()
        setup()

    }

    // Private

    private fun localize() {

        binding.buttonPrev.text = getText(viewModel.previousText)
        binding.buttonNext.text = getText(viewModel.nextText)
    }

    private fun setup() {

        // Adapter
        slider()

        // Dots
        dots(0)

        // Buttons

        binding.buttonPrev.secondary {
            selection -= 1
            binding.viewPagerOnboarding.setCurrentItem(selection, true)
        }

        binding.buttonNext.primary {
            if (selection == viewModel.pages - 1) {
                PreferencesProvider.set(this, PreferencesKey.ONBOARDING, true)
                finish()
            } else {
                selection += 1
                binding.viewPagerOnboarding.setCurrentItem(selection, true)
            }
        }
    }

    private fun slider() {


        binding.viewPagerOnboarding.adapter = OnboardingPageAdapter(this, viewModel.data)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                dots(position)
                selection = position

                binding.buttonPrev.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
                binding.buttonNext.text = getText(if (position == viewModel.pages - 1) viewModel.understoodText else viewModel.nextText)
            }
        })
    }

    private fun dots(position: Int) {
        dots = arrayOfNulls(viewModel.pages)
        binding.layoutDots.removeAllViews()
        for (index in dots.indices) {
            dots[index] = TextView(this)
            dots[index]?.text = Html.fromHtml("•")
            dots[index]?.textSize = 35f
            dots[index]?.setTextColor(getColor(if (index == position) R.color.primary else R.color.primary_shadow))
            binding.layoutDots.addView(dots[index])
        }
    }

}