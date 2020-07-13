package com.angcyo.uicore.demo.accessibility

import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityInterceptor
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.action.ActionException
import com.angcyo.core.component.accessibility.isActionStart

/**
 * 自动显示浮窗的拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseFloatInterceptor : BaseAccessibilityInterceptor() {

//    /**显示通知*/
//    open fun notify(title: CharSequence? = null, content: CharSequence? = null) {
//        if (!actionStatus.isActionInit()) {
//            sendNotify("${title}($actionIndex/${actionList.size})", content)
//        } else {
//            sendNotify(title, content)
//        }
//    }

    override fun onServiceConnected(service: BaseAccessibilityService) {
        super.onServiceConnected(service)
        AccessibilityWindow.show("已准备", 0)
    }

    override fun onIntervalStart(delay: Long) {
        super.onIntervalStart(delay)
        updateWindow()
    }

    override fun onDoActionFinish(action: BaseAccessibilityAction?, error: ActionException?) {
        updateWindow()
        super.onDoActionFinish(action, error)
    }

    override fun onDestroy() {
        if (actionStatus.isActionStart()) {
            AccessibilityWindow.show("中止", 0)
        }
        super.onDestroy()
    }

    fun updateWindow() {
        when (actionStatus) {
            ACTION_STATUS_INIT -> {
                if (actionIndex < 0) {
                    AccessibilityWindow.show("就绪", intervalDelay)
                } else {
                    AccessibilityWindow.show(
                        "$actionIndex/${actionList.size}",
                        intervalDelay
                    )
                }
            }
            ACTION_STATUS_ING -> {
                if (actionIndex != -1) {
                    AccessibilityWindow.show(
                        "$actionIndex/${actionList.size}",
                        intervalDelay
                    )
                } else {
                    AccessibilityWindow.show("等待", intervalDelay)
                }
            }
            ACTION_STATUS_FINISH -> AccessibilityWindow.show("已完成", 0)
            ACTION_STATUS_ERROR -> AccessibilityWindow.show("异常", 0)
            ACTION_STATUS_DESTROY -> AccessibilityWindow.show("结束", 0)
        }
    }
}