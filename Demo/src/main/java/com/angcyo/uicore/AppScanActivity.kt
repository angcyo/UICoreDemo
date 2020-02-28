package com.angcyo.uicore

import com.angcyo.qrcode.CodeScanActivity

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/28
 */

class AppScanActivity : CodeScanActivity() {
    override fun handleDecode(data: String): Boolean {
        return super.handleDecode(data)
    }
}