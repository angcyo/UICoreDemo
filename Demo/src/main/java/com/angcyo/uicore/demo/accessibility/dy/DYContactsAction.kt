package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.clickByText
import com.angcyo.core.component.accessibility.haveNodeOrText

/**
 * 关闭抖音 通讯录好友
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/28
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class DYContactsAction : BaseAccessibilityAction() {
    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        if (service.haveNodeOrText("发现通讯录好友")) {

            return service.clickByText("取消", event).apply {
                DYLikeInterceptor.log(
                    "发现抖音页[发现通讯录好友], 正在点击[取消] :$this"
                )
            }
        }
        return super.doActionWidth(action, service, event)
    }
}