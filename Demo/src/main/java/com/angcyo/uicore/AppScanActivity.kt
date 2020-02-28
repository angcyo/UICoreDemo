package com.angcyo.uicore

import android.app.Activity
import android.content.Intent
import com.angcyo.base.dslAHelper
import com.angcyo.noAnim
import com.angcyo.putData
import com.angcyo.qrcode.CodeScanActivity
import com.angcyo.uicore.demo.QrCodeDemo

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/28
 */

class AppScanActivity : CodeScanActivity() {

    override fun onBackPressed() {
        //super.onBackPressed()
        dslAHelper {
            start(MainActivity::class.java) {
                forceSingleTask = true
                noAnim()
            }
            finish()
        }
    }

    override fun handleDecode(data: String): Boolean {

        dslAHelper {
            start(QrCodeDemo::class.java) {
                intent.putData(data)
            }
        }

        val intent = Intent()
        intent.putExtra(KEY_DATA, data)
        setResult(Activity.RESULT_OK, intent)
        finish()
        return false
    }
}