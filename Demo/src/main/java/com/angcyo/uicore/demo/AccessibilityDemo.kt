package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.component.accessibility.AccessibilityPermission
import com.angcyo.core.component.accessibility.LogAccessibilityInterceptor
import com.angcyo.core.component.accessibility.StartAppAccessibilityInterceptor
import com.angcyo.core.component.accessibility.install
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.component.appBean
import com.angcyo.library.ex.copy
import com.angcyo.library.ex.openApp
import com.angcyo.library.ex.string
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.accessibility.DouYinInterceptor
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class AccessibilityDemo : AppDslFragment() {

    companion object {
        const val DY_PACKAGE_NAME = "com.ss.android.ugc.aweme"
        const val KS_PACKAGE_NAME = "com.smile.gifmaker"
    }

    val douYinInterceptor = DouYinInterceptor()

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)

        StartAppAccessibilityInterceptor().install()
        LogAccessibilityInterceptor().apply {
            enable = false
            install()
        }

        renderDslAdapter(true) {
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

                        douYinInterceptor.install()

                        fContext().openApp(DY_PACKAGE_NAME)
                    }
                    itemHolder.click(R.id.dy_button2) {
                        itemHolder.tv(R.id.dy_edit2)?.string()?.copy()

                        douYinInterceptor.install()

                        fContext().openApp(DY_PACKAGE_NAME)
                    }
                    itemHolder.click(R.id.ks_button) {
                        itemHolder.tv(R.id.ks_edit)?.string()?.copy()

                        //douYinInterceptor.install()

                        fContext().openApp(KS_PACKAGE_NAME)
                    }

                    //tip
                    itemHolder.tv(R.id.lib_text_view)?.text = span {
                        append(DY_PACKAGE_NAME.appBean().string("未安装[抖音]"))
                        appendln()
                        append(KS_PACKAGE_NAME.appBean().string("未安装[快手]"))
                    }
                }
            }
        }

    }
}