package com.angcyo.uicore.activity

import android.content.Intent
import android.os.Bundle
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.base.find
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.library.ex.nowTimeString
import com.angcyo.putData
import com.angcyo.putOriginIntent
import com.angcyo.uicore.MainActivity

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/08
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class NfcHandleActivity : BaseCoreAppCompatActivity() {

    companion object {
        var JUMP_COUNT = 0
    }

    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)
        dslFHelper {
            restore(NfcInfoDemo::class)
        }
        if (JUMP_COUNT++ % 2 == 1) {
            //跳板
            dslAHelper {
                finishSelf = true
                startFragment(MainActivity::class.java, NfcInfoDemo::class.java) {
                    putOriginIntent(getIntent())
                    putData("test", nowTimeString())
                }
            }
        }
    }

    override fun onHandleIntent(intent: Intent, fromNewIntent: Boolean) {
        super.onHandleIntent(intent, fromNewIntent)
        supportFragmentManager.find(NfcInfoDemo::class)?.updateInfo(intent)
    }
}