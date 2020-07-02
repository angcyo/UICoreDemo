package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.back

/**
 * 抖音点赞/关注/评论 完成后的收尾工作[Action], 回退界面到抖音主页
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
            onActionFinish()
        } else {
            service.back()
        }
    }
}