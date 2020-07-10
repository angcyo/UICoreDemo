package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*

/**
 * 快手分享对话框
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSShareAction : BaseAccessibilityAction() {

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveLook = false

        service.findNode {
            if (it.haveText("去看看")) {
                haveLook = true
            }
        }

        return haveLook
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {

        var titleNode: AccessibilityNodeInfoCompat? = null
        var clickNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
            if (it.haveText("的作品")) {
                titleNode = it
            }

            if (it.haveText("去看看")) {
                clickNode = it
            }
        }

        clickNode?.apply {
            val result = click()

            KSLikeInterceptor.log("快手分享[${titleNode?.text}], 点击[去看看] :$result")

            if (result) {
                doActionFinish()
            }
        }
    }

    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var titleNode: AccessibilityNodeInfoCompat? = null
        var clickNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
            if (it.haveText("的作品")) {
                titleNode = it
            }

            if (it.haveText("去看看")) {
                clickNode = it
            }
        }

        var result = false

        if (titleNode != null) {
            clickNode?.apply {
                result = click()
            }
        }

        return result || super.doActionWidth(action, service, event)
    }
}