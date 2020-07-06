package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.intervalMode
import com.angcyo.library.ex.openApp
import com.angcyo.uicore.demo.accessibility.BaseFloatInterceptor

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/01
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseDYInterceptor : BaseFloatInterceptor() {
    init {
        filterPackageNameList.add(DYLikeInterceptor.DY_PACKAGE_NAME)

        actionOtherList.add(DYPrivacyAction())
        actionOtherList.add(DYPermissionsAction())
        actionOtherList.add(DYProtectAction())
        actionOtherList.add(DYGuideAction())
        actionOtherList.add(DYUpdateAction())
        actionOtherList.add(DYContactsAction())
        actionOtherList.add(DYShareAction())
        actionOtherList.add(DYBackAction())

        intervalMode()
    }

    override fun onActionStart() {
        super.onActionStart()
        DYLikeInterceptor.DY_PACKAGE_NAME.openApp()
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