package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import com.angcyo.uicore.dslitem.tx

/**
 * 抖音评论[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/02
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class DYCommentAction : BaseDYVideoDetailAction() {
    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)
        if (isCommentLayoutShow) {
            //输入框node
            var editNode: AccessibilityNodeInfoCompat? = null

            service.findNode {
                if (it.isEditText() && it.haveText("留下你的精彩评论吧")) {
                    editNode = it
                }
            }

            editNode?.apply {

                val comment: String = tx()

                DYLikeInterceptor.log("正在输入评论[$comment] :${setNodeText(comment)}")

                val click: Boolean = click()
                DYLikeInterceptor.log("正在点击输入框, 弹出评论 :${click}")

                if (click) {
                    accessibilityInterceptor?.actionList?.forEach {
                        if (it is DYCommentSendAction) {
                            //将输入的评论, 传给[DYCommentSendAction], 很重要!
                            it.commentText = comment
                        }
                    }
                    doActionFinish()
                }
            }
        } else {
            // 弹出评论

            //用于执行点赞的node
            var commentClickNode: AccessibilityNodeInfoCompat? = null

            service.rootNodeInfo(event)?.findNode {

                //识别评论点击node
                if (it.haveText("评论") &&
                    it.haveText("按钮")
                ) {
                    if (it.isClickable) {
                        commentClickNode = it
                    }
                }

                -1
            }

            commentClickNode?.apply {
                DYLikeInterceptor.log("正在打开评论对话框 :${unwrap().click()}")
            }
        }
    }
}