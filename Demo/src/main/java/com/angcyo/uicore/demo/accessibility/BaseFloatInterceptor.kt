package com.angcyo.uicore.demo.accessibility

import com.angcyo.core.component.accessibility.*

/**
 * 自动显示浮窗的拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseFloatInterceptor : BaseAccessibilityInterceptor() {

    /**显示通知*/
    open fun notify(title: CharSequence? = null, content: CharSequence? = null) {
        if (!actionStatus.isActionInit()) {
            sendNotify("${title}($actionIndex/${actionList.size})", content)
        } else {
            sendNotify(title, content)
        }
    }

    override fun onServiceConnected(service: BaseAccessibilityService) {
        super.onServiceConnected(service)
        AccessibilityWindow.show("已准备", 0)
    }

    override fun onIntervalStart() {
        if (!actionStatus.isActionInit()) {
            AccessibilityWindow.show("$actionIndex/${actionList.size}", intervalDelay)
        }
    }

    override fun onInterval() {
        super.onInterval()
        onIntervalStart()
        if (actionStatus.isActionFinish()) {
            AccessibilityWindow.show("已完成", 0)
        } else if (actionStatus.isActionError()) {
            AccessibilityWindow.show("异常", 0)
        }
    }
}