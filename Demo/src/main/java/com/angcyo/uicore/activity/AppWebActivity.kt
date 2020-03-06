package com.angcyo.uicore.activity

import android.content.Intent
import android.os.Bundle
import com.angcyo.library.L
import com.angcyo.tbs.core.TbsWebActivity

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/01
 */

class AppWebActivity : TbsWebActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onHandleIntent(intent: Intent, fromNew: Boolean) {
        super.onHandleIntent(intent, fromNew)

        L.d("$intent ${intent.data}")
    }
}