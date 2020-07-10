package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.clickByText
import com.angcyo.core.component.accessibility.haveNodeOrText
import com.angcyo.uicore.demo.accessibility.dy.DYLikeInterceptor

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
        event: AccessibilityEvent?
    ): Boolean {
        if (service.haveNodeOrText("青少年模式")) {

            return service.clickByText("我知道了", event).apply {
                DYLikeInterceptor.log("发现抖音页[青少年模式], 正在点击[我知道了] :$this")
            }
        }
        return super.doActionWidth(action, service, event)
    }
}