package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/05/28
 */
class MagnifierDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_magnifier) { itemHolder, itemPosition, adapterItem, payloads ->

            }
        }
    }
}