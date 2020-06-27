package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*

/**
 * 关闭抖音青少年保护对话框[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYProtectAction : BaseAccessibilityAction() {
    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent
    ): Boolean {
        if (event.isClassNameContains("dialog") || event.haveText("青少年模式")) {

            return service.clickByText("我知道了", event).apply {
                DouYinInterceptor.log("发现抖音页[青少年模式], 正在点击[我知道了] :$this")
            }
        }
        return super.doActionWidth(action, service, event)
    }
}