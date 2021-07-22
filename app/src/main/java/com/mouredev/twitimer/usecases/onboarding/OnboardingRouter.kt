package com.mouredev.twitimer.usecases.onboarding

import android.content.Context
import android.content.Intent
import com.mouredev.twitimer.usecases.base.BaseActivityRouter

/**
 * Created by MoureDev by Brais Moure on 20/6/21.
 * www.mouredev.com
 */
class OnboardingRouter: BaseActivityRouter {

    override fun intent(activity: Context): Intent = Intent(activity, OnboardingActivity::class.java)

}