package com.angcyo.uicore.demo

import android.accessibilityservice.GestureDescription
import android.os.Build
import android.os.Bundle
import com.angcyo.core.component.accessibility.*
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.component.appBean
import com.angcyo.library.ex.copy
import com.angcyo.library.ex.openApp
import com.angcyo.library.ex.string
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.accessibility.AccessibilityWindow
import com.angcyo.uicore.demo.accessibility.dy.DYLikeInterceptor
import com.angcyo.uicore.demo.accessibility.dy.DYUserInterceptor
import com.angcyo.uicore.demo.accessibility.ks.KSLikeInterceptor
import com.angcyo.uicore.demo.accessibility.ks.KSUserInterceptor
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span

/**
 * 无障碍服务测试demo
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

    val douYinInterceptor = DYLikeInterceptor()
    val douYinUserInterceptor = DYUserInterceptor()

    val ksLikeInterceptor = KSLikeInterceptor()
    val ksUserInterceptor = KSUserInterceptor()

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        //GestureInterceptor().install()

        StartAppAccessibilityInterceptor().install()
//        LogAccessibilityInterceptor().apply {
//            enable = false
//            install()
//        }

        //记录所有窗口变化, 以及窗口上所有节点信息
        LogWindowAccessibilityInterceptor().apply {
            filterPackageNameList.add(DY_PACKAGE_NAME)
            filterPackageNameList.add(KS_PACKAGE_NAME)
            logAllWindow = !BuildConfig.DEBUG
            install()
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)

        if (AccessibilityPermission.haveAccessibilityService(fContext())) {
            AccessibilityWindow.show("准备", 0)
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

                    //抖音
                    itemHolder.click(R.id.dy_login_button) {
                        douYinUserInterceptor.restart()
                        douYinUserInterceptor.install()

                        fContext().openApp(DY_PACKAGE_NAME)
                    }
                    itemHolder.click(R.id.dy_button) {
                        itemHolder.tv(R.id.dy_edit)?.string()?.copy()

                        douYinInterceptor.restart()
                        douYinInterceptor.install()

                        fContext().openApp(DY_PACKAGE_NAME)
                    }
                    itemHolder.click(R.id.dy_button2) {
                        itemHolder.tv(R.id.dy_edit2)?.string()?.copy()

                        douYinInterceptor.restart()
                        douYinInterceptor.install()

                        fContext().openApp(DY_PACKAGE_NAME)
                    }

                    //快手
                    itemHolder.click(R.id.ks_login_button) {
                        ksUserInterceptor.install()
                        fContext().openApp(KS_PACKAGE_NAME)
                    }

                    itemHolder.click(R.id.ks_button) {
                        itemHolder.tv(R.id.ks_edit)?.string()?.copy()

                        ksLikeInterceptor.install()
                        fContext().openApp(KS_PACKAGE_NAME)
                    }

                    itemHolder.click(R.id.ks_button2) {
                        itemHolder.tv(R.id.ks_edit2)?.string()?.copy()

                        ksLikeInterceptor.install()
                        fContext().openApp(KS_PACKAGE_NAME)
                    }

                    //tip
                    itemHolder.tv(R.id.lib_text_view)?.text = span {
                        append("${DYLikeInterceptor.dyUserName}/${KSLikeInterceptor.ksUserName}")
                        appendln()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            append("GestureDuration:${GestureDescription.getMaxGestureDuration()}")
                            appendln()
                            append("MaxStrokeCount:${GestureDescription.getMaxStrokeCount()}") //最大支持多少个手指
                            appendln()
                        }
                        append(DY_PACKAGE_NAME.appBean().string("未安装[抖音]"))
                        appendln()
                        append(KS_PACKAGE_NAME.appBean().string("未安装[快手]"))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RAccessibilityService.clearInterceptor()
    }
}