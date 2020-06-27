package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService

/**
 * 抖音点赞[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYLikeAction : BaseAccessibilityAction() {
    override fun checkEvent(service: BaseAccessibilityService, event: AccessibilityEvent): Boolean {
        return super.checkEvent(service, event)
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent) {
        super.doAction(service, event)
    }
}