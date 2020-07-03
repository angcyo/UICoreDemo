package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.component.accessibility.action.ActionException
import com.angcyo.core.vmCore

/**
 * 获取快手登录用户名
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSUserInterceptor : BaseKSInterceptor() {
    init {
        intervalMode()

        actionList.add(KSLoginAction())
        actionList.add(KSGetUserAction())
    }

    override fun onDoAction(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService?,
        event: AccessibilityEvent?
    ) {
        sendNotify("快手账号获取[${KSLikeInterceptor.ksUserName}]", "正在执行:${action.getActionTitle()}")
        super.onDoAction(action, service, event)
    }

    override fun onActionFinish(error: ActionException?) {
        if (actionStatus.isActionFinish()) {
            openApp()
            vmCore<KSModel>().userNameData.value?.let {
                sendNotify(null, "快手号:${it}")
            }
        }
        super.onActionFinish(error)
    }
}