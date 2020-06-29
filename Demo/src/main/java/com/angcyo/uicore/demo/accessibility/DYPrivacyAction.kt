package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.library.L

/**
 * 关闭抖音隐私协议的[Action], 抖音个人信息保护对话框[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */

class DYPrivacyAction : BaseAccessibilityAction() {
    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        //L.i("${event.className}")
        //L.w(event.text)

        if ((service.haveNodeOrText("用户服务协议") && service.haveNodeOrText("隐私政策"))) {
            val result = service.clickByText("好的", event)

            DouYinInterceptor.log("发现抖音隐私页[用户服务协议], 正在点击[好的] :$result")

            //隐私协议对话框
            //service.rootNodeInfo(event)?.logNodeInfo()
            return result
        }
        return super.doActionWidth(action, service, event)
    }
}