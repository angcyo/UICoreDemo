package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*

/**
 * 快手弹出评论对话框
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class KSCommentAction : BaseAccessibilityAction() {

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        return KSLikeAction().checkEvent(service, event)
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        service.findNode {
            if (it.haveText("说点什么") && it.isClickable) {
                val result = it.click()
                KSLikeInterceptor.log("快手视频页, 弹出评论 :${result}")
                if (result) {
                    onActionFinish()
                }
            }
        }
    }
}