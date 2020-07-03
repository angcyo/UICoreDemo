package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.findNode
import com.angcyo.core.component.accessibility.haveText

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
}