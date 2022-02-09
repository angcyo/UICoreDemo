package com.angcyo.uicore.activity

import android.content.Intent
import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.base.find
import com.angcyo.core.activity.BaseCoreAppCompatActivity

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/08
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class NfcHandleActivity : BaseCoreAppCompatActivity() {

    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)
        dslFHelper {
            restore(NfcInfoDemo::class)
        }
    }

    override fun onHandleIntent(intent: Intent, fromNew: Boolean) {
        super.onHandleIntent(intent, fromNew)
        supportFragmentManager.find(NfcInfoDemo::class)?.updateInfo(intent)
    }

}