package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import com.angcyo.library._screenHeight
import com.angcyo.library._screenWidth
import com.angcyo.library.ex.dp

/**
 * 抖音点赞[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYLikeAction : BaseAccessibilityAction() {
    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveCommentEditView = false
        var haveLikeView = false
        var haveCommentView = false
        var haveShareView = false

        service.rootNodeInfo(event)?.findNode {
            when {
                it.haveText("留下你的精彩评论吧") -> haveCommentEditView = true
                it.haveText("喜欢") && it.isImageView() -> haveLikeView = true
                it.haveText("评论") && it.isImageView() -> haveCommentView = true
                it.haveText("分享") && it.isImageView() -> haveShareView = true
            }

            -1
        }

        val result: Boolean =
            haveCommentEditView && haveLikeView && haveCommentView && haveShareView

        //DouYinInterceptor.log("检查是否是抖音视频详情页:$result")

        return result
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {

        //用于执行点赞的node
        var likeClickNode: AccessibilityNodeInfoCompat? = null

        //用于判断是否点赞的node
        var likeNode: AccessibilityNodeInfoCompat? = null

        //视频标题的node
        var titleNode: AccessibilityNodeInfoCompat? = null

        service.rootNodeInfo(event)?.findNode {

            if (it.haveText("喜欢") &&
                it.haveText("按钮")
            ) {
                if (it.isClickable) {
                    likeClickNode = it
                } else {
                    likeNode = it
                }
            }

            if (it.haveText("@抖音小助手")) {
                titleNode = it
            } else if (titleNode == null) {
                it.getBoundsInScreen(AccessibilityHelper.tempRect)
                if (AccessibilityHelper.tempRect.top > _screenHeight / 2 &&
                    AccessibilityHelper.tempRect.width() > _screenWidth / 2 &&
                    AccessibilityHelper.tempRect.bottom > _screenHeight - 60 * dp
                ) {
                    //在屏幕下面, 底部距离 60dp的地方
                    titleNode = it
                }
            }

            -1
        }

        likeNode?.apply {
            val title = titleNode?.text

            if (isSelected || isChecked) {
                //已经点赞
                DouYinInterceptor.log("发现抖音视频详情页[${title}], 已点赞")
                onActionFinish(false)
            } else {
                val result = likeClickNode?.unwrap()?.click() ?: false
                DouYinInterceptor.log("发现抖音视频详情页[${title}], 正在点赞[${likeClickNode?.viewIdName()}] :$result")
            }
        }
    }
}