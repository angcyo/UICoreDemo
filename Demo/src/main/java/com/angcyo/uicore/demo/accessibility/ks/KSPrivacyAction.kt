package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*

/**
 * 关闭快手隐私政策
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */

class KSPrivacyAction : BaseAccessibilityAction() {
    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var targetNode: AccessibilityNodeInfoCompat? = null
        var clickNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
            if (it.haveText("隐私政策")) {
                targetNode = it
            }
            if (it.haveText("同意并继续")) {
                clickNode = it.getClickParent()
            }
        }

        return targetNode?.run { clickNode?.click() } ?: false
    }
}