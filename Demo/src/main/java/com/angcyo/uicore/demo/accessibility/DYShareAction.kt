package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*

/**
 * 抖音分享口令弹窗
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/27
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYShareAction : BaseAccessibilityAction() {

    override fun checkEvent(service: BaseAccessibilityService, event: AccessibilityEvent): Boolean {
        return event.haveText("检测到") ||
                event.haveText("检测到口令") ||
                event.haveText("检测到链接") ||
                event.haveText("打开看看") ||
                event.haveText("前往")
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent) {
        super.doAction(service, event)

        val clickText = when {
            event.haveText("打开看看") -> "打开看看"
            event.haveText("前往") -> "前往"
            else -> null
        }

        if (clickText == null) {
            DouYinInterceptor.log(
                "发现抖音口令分享页[${event.getLikeText("的作品").firstOrNull()}], 未识别到跳转按钮."
            )
            actionFinish?.invoke(true)
        } else {
            val result = service.clickByText(clickText, event)

            DouYinInterceptor.log(
                "发现抖音口令分享页[${event.getLikeText("的作品").firstOrNull()}], 正在点击[$clickText] :$result"
            )

            if (result) {
                //执行完成
                actionFinish?.invoke(false)
            }
        }
    }
}