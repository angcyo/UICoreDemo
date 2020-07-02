package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.vmCore
import com.angcyo.library._screenHeight
import com.angcyo.library._screenWidth
import com.angcyo.library.component._delay

/**
 * 抖音登录状态检查, 并获取用户[抖音号：] [Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/30
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYLoginAction : BaseAccessibilityAction() {

    companion object {
        const val dy = "抖音号："
    }

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveHome = false
        var haveCamera = false
        var haveMe = false
        var haveMessage = false

        //判断是否是主页
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

        return haveHome && haveCamera && haveMe && haveMessage
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)

        //检查抖音登录状态
        service.rootNodeInfo(event)?.findNode {
            if (it.haveText("通讯录好友")) {
                vmCore<DYModel>().loginData.postValue(true)
            } else if (it.haveText(dy) && it.bounds().width() > 0 && it.bounds().height() > 0) {
                //获取抖音账号
                val name: String? = it.text?.split(dy, ignoreCase = true)?.getOrNull(1)
                vmCore<DYModel>().login(name)

                name?.let {
                    DYLikeInterceptor.log("获取到抖音账号:[$it]")
                    onActionFinish()
                }

            } else if (it.haveText("我") &&
                it.bounds().centerX() > _screenWidth * 3 / 4 &&
                it.bounds().centerY() > _screenHeight * 5 / 6
            ) {
                //点击tab [我]
                val centerX = it.bounds().centerX()
                val centerY = it.bounds().centerY()
                service.gesture.click(centerX.toFloat(), centerY.toFloat())

                DYLikeInterceptor.log("点击抖音导航[我], touch:$centerX,$centerY :${service.gesture._isDispatched}")
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

    override fun getActionTitle(): String {
        return "获取抖音账号,如果需要请手动登录."
    }
}