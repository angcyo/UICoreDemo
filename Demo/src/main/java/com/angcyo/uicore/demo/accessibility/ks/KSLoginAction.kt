package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import com.angcyo.library.ex.dp

/**
 * 快手登录账号获取
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSLoginAction : BaseAccessibilityAction() {

    init {
        actionTitle = "获取快手账号,如果需要请手动登录."
    }

    //主页未登录情况下, 出现的左上角登录按钮
    fun AccessibilityNodeInfoCompat.loginNode(): AccessibilityNodeInfoCompat? {
        if (haveText("登录") &&
            isClickable &&
            bounds().centerX() <= 100 * dp &&
            bounds().centerY() <= 100 * dp
        ) {
            return this
        }
        return null
    }

    //主页登录情况下, 出现的左上角菜单按钮
    fun AccessibilityNodeInfoCompat.menuNode(): AccessibilityNodeInfoCompat? {
        if (haveText("菜单") &&
            isClickable &&
            bounds().centerX() <= 100 * dp &&
            bounds().centerY() <= 100 * dp
        ) {
            return this
        }
        return null
    }

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var loginNode: AccessibilityNodeInfoCompat? = null
        var menuNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
            //左上角的登录按钮
            loginNode = loginNode ?: it.loginNode()?.getClickParent()
            menuNode = menuNode ?: it.menuNode()?.getClickParent()
        }

        return loginNode != null || menuNode != null
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)

        var loginNode: AccessibilityNodeInfoCompat? = null
        var menuNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
            //左上角的登录按钮
            loginNode = loginNode ?: it.loginNode()?.getClickParent()

            menuNode = menuNode ?: it.menuNode()?.getClickParent()
        }

        loginNode?.apply {
            KSLikeInterceptor.log("快手主页, 点击[登录] :${click()}")
            return
        }

        menuNode?.apply {
            val result = click()
            KSLikeInterceptor.log("快手主页, 点击[菜单] :$result")
            if (result) {
                doActionFinish()
            }
            return
        }
    }
}