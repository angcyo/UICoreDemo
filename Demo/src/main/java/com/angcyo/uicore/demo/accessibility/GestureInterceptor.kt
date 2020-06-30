package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityInterceptor
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.action.PermissionsAction

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/30
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class GestureInterceptor : BaseAccessibilityInterceptor() {
    init {

        enableInterval = true
        intervalDelay = 4_000

        actionList.add(PermissionsAction())

        actionOtherList.add(DYGuideAction())
    }

    override fun onAccessibilityEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ) {
        super.onAccessibilityEvent(service, event)
    }

    override fun onNoOtherActionHandle(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ) {
        //super.onNoOtherActionHandle(service, event)
    }
}