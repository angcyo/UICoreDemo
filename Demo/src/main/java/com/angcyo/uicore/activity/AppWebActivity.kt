package com.angcyo.uicore.activity

import android.content.Intent
import android.os.Build
import com.angcyo.base.dslAHelper
import com.angcyo.library.L
import com.angcyo.library.ex.runningTasks
import com.angcyo.noAnim
import com.angcyo.setTargetIntent
import com.angcyo.tbs.core.TbsWebActivity
import com.angcyo.uicore.MainActivity


/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/01
 */

class AppWebActivity : TbsWebActivity() {

    override fun onHandleIntent(intent: Intent, fromNew: Boolean) {
        super.onHandleIntent(intent, fromNew)

        L.d("$intent ${intent.data}")

        runningTasks(1).firstOrNull()?.apply {
            val callingPackageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                baseActivity?.packageName
            } else {
                callingPackage
            }

            if (callingPackageName != packageName) {
                //从别的程序启动的[Activity]
                dslAHelper {
                    start(MainActivity::class.java) {
                        this.intent.setTargetIntent(intent)
                        finishSelf = true
                        finishWithFinish = true
                        finishToActivity = null
                        noAnim()
                    }
                }
            }
        }
    }
}