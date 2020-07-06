package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import kotlin.random.Random

/**
 * 抖音点赞[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYLikeAction : BaseDYVideoDetailAction() {

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {

        //用于执行点赞的node
        var likeClickNode: AccessibilityNodeInfoCompat? = null

        //用于判断是否点赞的node
        var likeNode: AccessibilityNodeInfoCompat? = null

        //关注node
        var attentionNode: AccessibilityNodeInfoCompat? = null

        service.rootNodeInfo(event)?.findNode {

            //识别点赞状态和点赞点击node
            if (it.haveText("喜欢") &&
                it.haveText("按钮")
            ) {
                if (it.isClickable) {
                    likeClickNode = it
                } else {
                    likeNode = it
                }
            }

            //关注
            if (it.isButton() &&
                it.contentDescription.contains("关注") &&
                it.isValid() &&
                it.isClickable
            ) {
                attentionNode = it
            }

            -1
        }

        val nextInt = Random.nextInt(2, 5)
        if (actionDoCount < nextInt) {
            DYLikeInterceptor.log("[双击 $actionDoCount]抖音视频详情页: ${service.gesture.double()}")

            onRandomIntervalDelay()
            return
        }

        if (actionDoCount < nextInt + 2) {
            attentionNode?.apply {
                DYLikeInterceptor.log(
                    "发现抖音视频详情页, 正在点击关注 :${unwrap().click()}"
                )
            }
            return
        }

        likeNode?.apply {

            if (isSelected || isChecked) {
                //已经点赞
                DYLikeInterceptor.log(
                    "发现抖音视频详情页[${videoTitle}], 已点赞"
                )
                onActionFinish()
            } else {
                val result = likeClickNode?.unwrap()?.click() ?: false
                DYLikeInterceptor.log(
                    "发现抖音视频详情页[${videoTitle}], 正在点赞[${likeClickNode?.viewIdName()}] :$result"
                )
            }
        }
    }
}