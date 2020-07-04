package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.library.ex.openApp
import com.angcyo.uicore.demo.accessibility.BaseFloatInterceptor

/**
 * 快手基类拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseKSInterceptor : BaseFloatInterceptor() {
    init {
        filterPackageNameList.add(KSLikeInterceptor.KS_PACKAGE_NAME)

        actionOtherList.add(KSPrivacyAction())
        actionOtherList.add(KSProtectAction())
        actionOtherList.add(KSGetUserAction())
        actionOtherList.add(KSBackAction())
        actionOtherList.add(KSShareAction())
    }

    override fun onLeavePackageName(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?,
        toPackageName: CharSequence?
    ) {
        super.onLeavePackageName(service, event, toPackageName)

        //会造成无限操作
//        toPackageName?.apply {
//            if (contains("system") || contains("google")) {
//                //no op
//            } else {
//                //回到快手
//                lastService?.openApp(KSLikeInterceptor.KS_PACKAGE_NAME)
//            }
//        }
    }
}