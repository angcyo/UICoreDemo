package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*

/**
 * 关闭没用的界面
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class KSBackAction : BaseAccessibilityAction() {

    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var result = false

        service.findNode {
            if (it.haveText("温馨提示")) {
                service.findNode {
                    if (it.haveText("取消")) {
                        result = result || it.getClickParent()?.click() ?: false
                    }
                }
            }
        }

        return result
    }
}