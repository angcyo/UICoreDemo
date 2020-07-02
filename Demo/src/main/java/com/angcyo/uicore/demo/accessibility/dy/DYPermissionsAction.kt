package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.action.PermissionsAction
import com.angcyo.core.component.accessibility.haveNode
import com.angcyo.core.component.accessibility.haveText

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/27
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYPermissionsAction : PermissionsAction() {

    override fun isPermissionsUI(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        val haveNode = service.haveNode("允许访问") || service.haveNode("要允许抖音短视频", event)
        val haveEvent = event?.haveText("要允许抖音短视频") ?: false
        return super.isPermissionsUI(service, event) || haveNode || haveEvent
    }

    override fun handlePermissionsAction(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        return super.handlePermissionsAction(action, service, event).apply {
            DYLikeInterceptor.log("发现抖音权限申请页, 正在点击[允许] :$this")
        }
    }
}