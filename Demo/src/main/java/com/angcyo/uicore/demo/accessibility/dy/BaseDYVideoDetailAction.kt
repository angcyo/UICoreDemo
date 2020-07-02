package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*

/**
 * 抖音视频详情基类[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
abstract class BaseDYVideoDetailAction : BaseAccessibilityAction() {
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
}