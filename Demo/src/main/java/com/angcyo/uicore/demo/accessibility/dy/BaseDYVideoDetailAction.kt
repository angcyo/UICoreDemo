package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import com.angcyo.library._screenHeight
import com.angcyo.library._screenWidth
import com.angcyo.library.ex.dp

/**
 * 抖音视频详情基类[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
abstract class BaseDYVideoDetailAction : BaseAccessibilityAction() {

    /**评论布局是否显示*/
    var isCommentLayoutShow: Boolean = false

    /**视频标题*/
    var videoTitle: CharSequence? = null

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveCommentEditView = false
        var haveLikeView = false
        var haveCommentView = false
        var haveShareView = false

        isCommentLayoutShow = false

        //视频标题的node
        var titleNode: AccessibilityNodeInfoCompat? = null

        service.rootNodeInfo(event)?.findNode {
            when {
                it.haveText("留下你的精彩评论吧") -> haveCommentEditView = true
                it.haveText("喜欢") && it.isImageView() -> haveLikeView = true
                it.haveText("评论") && it.isImageView() -> haveCommentView = true
                it.haveText("分享") && it.isImageView() -> haveShareView = true
            }

            //评论dialog是否显示
            if (it.haveText("条评论") && it.isTextView()) {
                isCommentLayoutShow = true
            }

            //视频标题
            if (it.haveText("@抖音小助手")) {
                titleNode = it
            } else if (titleNode == null) {
                it.getBoundsInScreen(AccessibilityHelper.tempRect)
                if (it.text != null && it.isTextView()) {

                    if (AccessibilityHelper.tempRect.top > _screenHeight / 2 &&
                        AccessibilityHelper.tempRect.width() > _screenWidth / 2 &&
                        AccessibilityHelper.tempRect.bottom >= _screenHeight - 110 * dp
                    ) {
                        //在屏幕下面, 底部距离 60dp的地方
                        titleNode = it
                    }
                }
            }

            -1
        }

        //视频标题
        if (titleNode != null) {
            videoTitle = titleNode?.text
        }

        val result = haveCommentEditView && haveLikeView && haveCommentView && haveShareView

        //DouYinInterceptor.log("检查是否是抖音视频详情页:$result")

        return result
    }
}