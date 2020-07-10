package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import com.angcyo.uicore.demo.accessibility.dy.DYLikeInterceptor

/**
 * 抖音发送评论[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/02
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class DYCommentSendAction : BaseAccessibilityAction() {

    /**输入框输入的文本*/
    var commentText: String? = null

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveCommentEdit = false
        var haveEmojiView = false

        val text = commentText

        service.findNode {
            if (!text.isNullOrEmpty() && it.haveText(text)) {
                haveCommentEdit = true
            }
        }

        return haveCommentEdit
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)
        //输入框node
        var editNode: AccessibilityNodeInfoCompat? = null

        //评论node
        var commentNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
            if (it.isEditText() && it.haveText(commentText ?: "留下你的精彩评论吧")) {
                editNode = it

                commentNode = it.parent?.getChildOrNull(3)
            }
        }

        editNode?.apply {
            if (commentNode == null) {
                DYLikeInterceptor.log("发现抖音视频评论页, 但是未找到评论按钮!")
            } else {
                val click = commentNode?.click() ?: false

                DYLikeInterceptor.log("发现抖音视频评论页, 正在点击评论 :${click}")

                if (click) {
                    doActionFinish()
                }
            }
        }
    }
}