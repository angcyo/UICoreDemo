package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.vmCore
import com.angcyo.library._screenHeight
import com.angcyo.library._screenWidth
import com.angcyo.library.component._delay
import com.angcyo.library.ex.dp

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

        /**抖音底部导航[首页]的[Node]*/
        fun tabHomeClickNode(node: AccessibilityNodeInfoCompat): AccessibilityNodeInfoCompat? {
            var result: AccessibilityNodeInfoCompat? = null
            if (node.haveText("首页")) {
                node.getClickParent()?.let {
                    it.bounds().apply {
                        if (centerX() > 0 &&
                            centerX() < _screenWidth * 1 / 5 &&
                            bottom + 20 * dp >= _screenHeight
                        ) {
                            result = it
                        }
                    }
                }
            }
            return result
        }

        /**抖音底部导航[消息]的[Node]*/
        fun tabMessageClickNode(node: AccessibilityNodeInfoCompat): AccessibilityNodeInfoCompat? {
            var result: AccessibilityNodeInfoCompat? = null
            if (node.haveText("消息")) {
                node.getClickParent()?.let {
                    it.bounds().apply {
                        if (centerX() > _screenWidth * 3 / 5 &&
                            centerX() < _screenWidth * 4 / 5 &&
                            bottom + 20 * dp >= _screenHeight
                        ) {
                            result = it
                        }
                    }
                }
            }
            return result
        }

        /**抖音底部导航[我]的[Node]*/
        fun tabMeClickNode(node: AccessibilityNodeInfoCompat): AccessibilityNodeInfoCompat? {
            var result: AccessibilityNodeInfoCompat? = null
            if (node.haveText("我")) {
                node.getClickParent()?.let {
                    it.bounds().apply {
                        if (centerX() > _screenWidth * 4 / 5 &&
                            centerX() < _screenWidth &&
                            bottom + 20 * dp >= _screenHeight
                        ) {
                            result = it
                        }
                    }
                }
            }
            return result
        }

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
        service.findNode {
            if (!haveHome) {
                tabHomeClickNode(it)?.let {
                    haveHome = true
                }
            }
            if (!haveMessage) {
                tabMessageClickNode(it)?.let {
                    haveMessage = true
                }
            }
            if (!haveMe) {
                tabMeClickNode(it)?.let {
                    haveMe = true
                }
            }

            if (!haveCamera) {
                if (it.haveText("拍摄")) {
                    haveCamera = true
                }
            }
        }

        return haveHome && haveCamera && haveMe && haveMessage
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)

        //检查抖音登录状态

        var meClickNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
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

            } else {
                meClickNode = meClickNode ?: tabMeClickNode(it)
            }
        }

        if (!vmCore<DYModel>().isLogin()) {
            meClickNode?.let { node ->
                if (node.isSelected) {
                    DYLikeInterceptor.log("已是抖音Tab[我], 尝试[flingDown] :${service.gesture.flingDown()}")
                } else {
                    DYLikeInterceptor.log("点击抖音导航[我] :${node.click()}")
                }
            }
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