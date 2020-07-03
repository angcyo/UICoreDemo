package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import kotlin.random.Random.Default.nextInt

/**
 * 抖音点赞/关注/评论 完成后的收尾工作[Action], 回退界面到抖音主页
 *
 * 随机操作模拟
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/02
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class DYLikeFinishAction : BaseAccessibilityAction() {
    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        return true
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {

        if (DYLoginAction().checkEvent(service, event)) {
            //抖音首页

            var homeTabClickNode: AccessibilityNodeInfoCompat? = null
            var homeMessageClickNode: AccessibilityNodeInfoCompat? = null

            service.findNode {
                homeTabClickNode = homeTabClickNode ?: DYLoginAction.tabHomeClickNode(it)
                homeMessageClickNode = homeMessageClickNode ?: DYLoginAction.tabMessageClickNode(it)
            }

            when (nextInt(4)) {
                0 -> {
                    //回到首页
                    homeTabClickNode?.click()
                }
                1 -> {
                    //回到消息
                    homeMessageClickNode?.click()
                }
                else -> {
                    if (homeTabClickNode?.isSelected == true) {
                        DYLikeInterceptor.log("抖音tab[首页],随机操作: [${service.gesture.randomization()}] $actionDoCount")
                    } else if (homeMessageClickNode?.isSelected == true) {
                        DYLikeInterceptor.log("抖音tab[消息],随机操作: [${service.gesture.randomization()}] $actionDoCount")
                    }

                    checkFinish()
                }
            }
        } else {
            service.back()
        }
    }

    fun checkFinish() {
        if (actionDoCount > nextInt(10, 20)) {
            //随机操作[10-20)次, 然后结束
            onActionFinish()
        } else {
            randomIntervalDelay()
        }
    }
}