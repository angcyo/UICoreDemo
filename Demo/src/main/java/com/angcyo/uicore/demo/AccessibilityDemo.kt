package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.component.accessibility.AccessibilityPermission
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.copy
import com.angcyo.library.ex.openApp
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.string

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
                    itemHolder.cb(R.id.overlays_enable)?.isChecked =
                        AccessibilityPermission.haveDrawOverlays(fContext())
                    itemHolder.cb(R.id.accessibility_enable)?.isChecked =
                        AccessibilityPermission.haveAccessibilityService(fContext())


                    itemHolder.click(R.id.overlays_button) {
                        AccessibilityPermission.openOverlaysActivity(fContext())
                    }
                    itemHolder.click(R.id.accessibility_button) {
                        AccessibilityPermission.openAccessibilityActivity(fContext())
                    }
                    itemHolder.click(R.id.check_button) {
                        itemHolder.tv(R.id.lib_text_view)?.text =
                            "${AccessibilityPermission.check(fContext())}"
                    }


                    itemHolder.click(R.id.dy_button) {
                        itemHolder.tv(R.id.dy_edit)?.string()?.copy()
                        fContext().openApp("com.ss.android.ugc.aweme")
                    }
                    itemHolder.click(R.id.ks_button) {
                        itemHolder.tv(R.id.ks_edit)?.string()?.copy()
                        fContext().openApp("com.smile.gifmaker")
                    }
                }
            }
        }
    }
}