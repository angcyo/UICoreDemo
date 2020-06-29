package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.action.PermissionsAction

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/27
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYPermissionsAction : PermissionsAction() {
    override fun handlePermissionsAction(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        return super.handlePermissionsAction(action, service, event).apply {
            DouYinInterceptor.log("发现抖音权限申请页, 正在点击[允许] :$this")
        }
    }
}