package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*

/**
 * 关闭快手青少年保护对话框
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class KSProtectAction : BaseAccessibilityAction() {
    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var result = false

        service.findNode {
            if (it.haveText("青少年模式")) {
                service.findNode {
                    if (it.haveText("我知道了")) {
                        result = result || it.getClickParent()?.click() ?: false
                    }
                }
            }
        }

        return super.doActionWidth(action, service, event)
    }
}