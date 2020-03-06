package com.angcyo.uicore.activity

import android.content.Intent
import com.angcyo.library.L
import com.angcyo.tbs.core.TbsWebActivity

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
    }
}