package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.vmCore
import com.angcyo.library._screenHeight
import com.angcyo.library._screenWidth
import com.angcyo.library.component._delay
import com.angcyo.library.ex.isListEmpty

/**
 * 抖音登录状态检查
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/30
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYLoginAction : BaseAccessibilityAction() {

    val dy = "抖音号："

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveHome = false
        var haveCamera = false
        var haveMe = false
        var haveMessage = false

        service.rootNodeInfo(event)?.findNode {
            if (it.haveText("首页")) {
                haveHome = true
            } else if (it.haveText("拍摄")) {
                haveCamera = true
            } else if (it.haveText("消息") &&
                it.bounds().centerX() > _screenWidth * 1 / 2 &&
                it.bounds().centerY() > _screenHeight * 5 / 6
            ) {
                haveMessage = true
            } else if (it.haveText("我") &&
                it.bounds().centerX() > _screenWidth * 3 / 4 &&
                it.bounds().centerY() > _screenHeight * 5 / 6
            ) {
                haveMe = true
            }
            -1
        }

        return !service.rootNodeInfo(event)?.findNode {
            if (it.haveText(dy)) {
                0
            } else {
                -1
            }
        }.isListEmpty() || (haveHome && haveCamera && haveMe && haveMessage)
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)

        //检查抖音登录状态
        service.rootNodeInfo(event)?.findNode {
            if (it.haveText("通讯录好友")) {
                vmCore<DYModel>().loginData.postValue(true)
            } else if (it.haveText(dy)) {
                vmCore<DYModel>().userNameData.postValue(
                    it.text?.split(dy, ignoreCase = true)?.getOrNull(1)
                )
                vmCore<DYModel>().loginData.postValue(true)

                onActionFinish()
            } else if (it.haveText("我") &&
                it.bounds().centerX() > _screenWidth * 3 / 4 &&
                it.bounds().centerY() > _screenHeight * 5 / 6
            ) {
                //点击tab [我]
                val centerX = it.bounds().centerX()
                val centerY = it.bounds().centerY()
                service.gesture.click(centerX.toFloat(), centerY.toFloat())

                DouYinInterceptor.log("点击抖音导航[我], touch:$centerX,$centerY :${service.gesture._isDispatched}")
            }
            -1
        }
    }

    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        return service.rootNodeInfo(event)?.findNodeByText("请输入手机号")?.run {
            find { it.isEditable }?.run {
                val result = unwrap().setNodeText("18888888888")

                _delay {
                    service.rootNodeInfo(event)?.findNodeByText("获取短信验证码")?.firstOrNull()?.let {
                        it.getBoundsInScreen(AccessibilityHelper.tempRect)
                        service.gesture.click(
                            AccessibilityHelper.tempRect.centerX().toFloat(),
                            AccessibilityHelper.tempRect.centerY().toFloat()
                        )
                    }
                }

                result
            }
        } ?: super.doActionWidth(action, service, event)
    }
}