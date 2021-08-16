package com.mouredev.twitimer.usecases.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mouredev.twitimer.R
import com.mouredev.twitimer.usecases.base.BaseActivityRouter

/**
 * Created by MoureDev by Brais Moure on 10/8/21.
 * www.mouredev.com
 */
class SettingsRouter: BaseActivityRouter {

    override fun intent(activity: Context): Intent = Intent(activity, SettingsActivity::class.java)

    override fun launch(activity: Context) {
        activity.startActivity(intent(activity))
        (activity as Activity).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

}