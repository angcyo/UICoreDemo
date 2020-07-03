package com.angcyo.uicore.demo.accessibility.dy

import com.angcyo.core.component.accessibility.*
import com.angcyo.uicore.demo.accessibility.AccessibilityWindow

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/01
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseDYInterceptor : BaseAccessibilityInterceptor() {
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

    override fun onServiceConnected(service: BaseAccessibilityService) {
        super.onServiceConnected(service)
        AccessibilityWindow.show("已准备", 0)
    }

    override fun onIntervalStart() {
        if (!actionStatus.isActionInit()) {
            AccessibilityWindow.show("$actionIndex/${actionList.size}", intervalDelay)
        }
    }

    override fun onInterval() {
        super.onInterval()
        onIntervalStart()
        if (actionStatus.isActionFinish()) {
            AccessibilityWindow.show("已完成", 0)
        } else if (actionStatus.isActionError()) {
            AccessibilityWindow.show("异常", 0)
        }
    }
}