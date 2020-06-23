package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.component.accessibility.AccessibilityHelper
import com.angcyo.dsladapter.renderItem
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class AccessibilityDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.item_accessibility_demo

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.cb(R.id.cb_enable)?.isChecked = false

                    itemHolder.click(R.id.set_button) {
                        AccessibilityHelper.openAccessibilityActivity(fContext())
                    }
                }
            }
        }
    }
}