package com.mouredev.twitimer.usecases.onboarding.page

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mouredev.twitimer.model.domain.Onboarding

/**
 * Created by MoureDev by Brais Moure on 20/6/21.
 * www.mouredev.com
 */
class OnboardingPageAdapter(val context: AppCompatActivity, var pages: List<Onboarding>): FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        val page = pages[position]
        return OnboardingPageFragment.fragment(page)
    }

}