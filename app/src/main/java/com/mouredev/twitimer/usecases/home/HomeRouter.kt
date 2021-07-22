package com.mouredev.twitimer.usecases.home

import android.content.Context
import android.content.Intent
import com.mouredev.twitimer.usecases.base.BaseActivityRouter

/**
 * Created by MoureDev by Brais Moure on 5/18/21.
 * www.mouredev.com
 */
class HomeRouter: BaseActivityRouter {

    override fun intent(activity: Context): Intent = Intent(activity, HomeActivity::class.java)

}