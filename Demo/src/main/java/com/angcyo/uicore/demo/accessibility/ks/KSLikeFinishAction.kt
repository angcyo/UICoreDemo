package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import kotlin.random.Random.Default.nextInt

/**
 * 快手点赞/关注/评论 完成后的收尾工作[Action], 回退界面到快手主页
 *
 * 随机操作模拟
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/02
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSLikeFinishAction : BaseAccessibilityAction() {

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        return true
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {

        if (KSLoginAction().checkEvent(service, event)) {
            //快手首页

            var attentionTabClickNode: AccessibilityNodeInfoCompat? = null
            var findTabClickNode: AccessibilityNodeInfoCompat? = null

            service.findNode {
                if (it.haveText("关注") && it.isClickable && it.isValid()) {
                    attentionTabClickNode = it
                }
                if (it.haveText("发现") && it.isClickable && it.isValid()) {
                    findTabClickNode = it
                }
            }

            val nextInt = nextInt(4)
            if (nextInt == 0 && findTabClickNode?.isSelected == false) {
                //回到发现
                KSLikeInterceptor.log("快手收尾操作, 点击tab[发现] :${findTabClickNode?.click()} $actionDoCount")
            } else if (nextInt == 1 && attentionTabClickNode?.isSelected == false) {
                //回到关注
                KSLikeInterceptor.log("快手收尾操作, 点击tab[关注] :${attentionTabClickNode?.click()} $actionDoCount")
            } else {
                if (findTabClickNode?.isSelected == true) {
                    KSLikeInterceptor.log("快手tab[发现],随机操作: [${service.gesture.randomization()}] $actionDoCount")
                } else if (attentionTabClickNode?.isSelected == true) {
                    KSLikeInterceptor.log("快手tab[关注],随机操作: [${service.gesture.randomization()}] $actionDoCount")
                }

                checkFinish()
            }
        } else {
            KSLikeInterceptor.log("快手收尾操作, 回退 :${service.back()} $actionDoCount")
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