package com.angcyo.uicore.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.angcyo.library.L
import com.angcyo.library.component.dslNotify
import com.angcyo.library.ex.simpleHash
import com.angcyo.uicore.demo.R

/**
 * https://developer.android.google.cn/guide/components/broadcasts.html
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/14
 */

class AppBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_DEMO = "app.action.demo"
    }

    /**一旦从此方法返回代码，系统便会认为该组件不再活跃。*/
    override fun onReceive(context: Context, intent: Intent?) {
        //val pendingResult = goAsync()
        //pendingResult.finish()
        L.i("${this.simpleHash()} 广播:$context $intent")

        dslNotify(context) {
            notifySmallIcon = R.drawable.ic_logo_small
            notifyTitle = "收到广播"
            notifyText = "${intent?.action}"

            styleBigText = "$intent"
        }
    }
}