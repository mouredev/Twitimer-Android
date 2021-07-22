package com.mouredev.twitimer.usecases.onboarding

import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.model.domain.Onboarding

/**
 * Created by MoureDev by Brais Moure on 20/6/21.
 * www.mouredev.com
 */
class OnboardingViewModel : ViewModel() {

    // Properties

    val data = arrayListOf(
        Onboarding(0, R.drawable.twitimer_icon, R.string.onboarding_page0_title, R.string.onboarding_page0_body),
        Onboarding(1, R.drawable.radio_microphone, R.string.onboarding_page1_title, R.string.onboarding_page1_body),
        Onboarding(2, R.drawable.examine, R.string.onboarding_page2_title, R.string.onboarding_page2_body),
        Onboarding(3, R.drawable.timer, R.string.onboarding_page3_title, R.string.onboarding_page3_body),
        Onboarding(4, R.drawable.risk_assesment, R.string.onboarding_page4_title, R.string.onboarding_page4_body)
    )

    val pages = data.size

    // Localization

    val understoodText = R.string.understood
    val previousText = R.string.previous
    val nextText = R.string.next

    // Public


}