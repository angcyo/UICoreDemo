package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.library._screenHeight

/**
 * 快手点赞/关注/评论[Action]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSLikeAction : BaseAccessibilityAction() {

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveLike = false
        var haveReport = false
        var haveShare = false

        service.findNode {
            if (it.haveText("喜欢")) {
                haveLike = true
            }
            if (it.haveText("举报")) {
                haveReport = true
            }
            if (it.haveText("分享")) {
                haveShare = true
            }
        }

        return haveLike && haveReport && haveShare
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {

        //视频标题
        val title = service.findNodeById("com.smile.gifmaker:id/label").firstOrNull()?.text

        //快手的点赞按钮无法获取selected状态, 这里只能模拟双击操作了

        if (actionDoCount < 3) {
            KSLikeInterceptor.log("[双击 $actionDoCount]快手视频[$title] :${service.gesture.double(y = _screenHeight / 4f)}")

            onRandomIntervalDelay()
            return
        }

        //关注
        service.findNode {
            if (it.haveText("关注")) {
                val result = it.getClickParent()?.click() ?: false
                KSLikeInterceptor.log("快手视频页[$title], 点击关注 :${result}")

                if (result) {
                    onActionFinish()
                }
            }
        }
    }
}