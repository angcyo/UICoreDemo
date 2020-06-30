package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.component.accessibility.action.ActionException

/**
 * 抖音分享口令弹窗
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/27
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYShareAction : BaseAccessibilityAction() {

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        return service.haveNodeOrText("检测到") ||
                service.haveNodeOrText("检测到口令") ||
                service.haveNodeOrText("检测到链接") ||
                service.haveNodeOrText("打开看看") ||
                service.haveNodeOrText("前往")
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)

        val clickText = when {
            service.haveNodeOrText("打开看看") -> "打开看看"
            service.haveNodeOrText("前往") -> "前往"
            else -> null
        }

        val likeText = service.getLikeText("的作品").firstOrNull()

        if (clickText == null) {
            DouYinInterceptor.log("发现抖音口令分享页[${likeText}], 未识别到跳转按钮.")
            onActionFinish(ActionException("未识别到跳转按钮"))
        } else {
            val result = service.clickByText(clickText, event)

            DouYinInterceptor.log("发现抖音口令分享页[${likeText}], 正在点击[$clickText] :$result")

            if (result) {
                //执行完成
                onActionFinish()
            }
        }
    }

    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        if (checkEvent(service, event)) {
            return service.clickById("com.ss.android.ugc.aweme:id/cxw", event).apply {
                DouYinInterceptor.log("发现多余的抖音口令分享页, 正在点击[关闭] :$this")
            }
        }
        return super.doActionWidth(action, service, event)
    }
}