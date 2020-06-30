package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.haveNodeOrText
import com.angcyo.core.component.accessibility.move
import com.angcyo.library.L
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

            service.gesture.flingDuration()
            service.gesture.touch(fX, fY, fX, tY)
            service.gesture.doIt()

            val result = service.gesture._isDispatched

            DouYinInterceptor.log("发现抖音引导页[滑动查看更多], fling:$fX,$fY $fX,$tY :$result")

            return result
        }

        if (event == null) {
            val screenWidth = _screenWidth
            val screenHeight = _screenHeight

            val fX = screenWidth / 2
            val fY = screenHeight * 3 / 5
            val tY = screenHeight * 2 / 5

            L.i("开始fling:[${fX},${fY}]->[${fX},${tY}]")
            //service.gesture.double(fX, 160)
            service.gesture.move(fX, fY, fX, tY) { gestureDescription, dispatched, canceled ->
                L.i(gestureDescription, " $dispatched $canceled")
            }
            val result = service.gesture._isCompleted
            L.w("结束fling:$result")
        }

        return super.doActionWidth(action, service, event)
    }
}