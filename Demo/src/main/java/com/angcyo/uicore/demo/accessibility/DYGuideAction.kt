package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.fling
import com.angcyo.core.component.accessibility.haveNodeOrText
import com.angcyo.library._screenHeight
import com.angcyo.library._screenWidth

/**
 * 抖音 滑动查看更多 action
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/26
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYGuideAction : BaseAccessibilityAction() {
    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        if (service.haveNodeOrText("滑动查看更多", event) ||
            service.haveNodeOrText("上滑查看更多", event)
        ) {
            //当前处理 [滑动查看更多] 引导界面

            val screenWidth = _screenWidth
            val screenHeight = _screenHeight

            val fX = screenWidth / 2
            val fY = screenHeight * 3 / 5
            val tY = screenHeight * 2 / 5

            var result = false

            service.fling(fX, fY, fX, tY) { gestureDescription, cancel ->
                result = !cancel
            }

            DouYinInterceptor.log("发现抖音引导页[滑动查看更多], fling:$fX,$fY $fX,$tY :$result")

            return result
        }
        return super.doActionWidth(action, service, event)
    }
}