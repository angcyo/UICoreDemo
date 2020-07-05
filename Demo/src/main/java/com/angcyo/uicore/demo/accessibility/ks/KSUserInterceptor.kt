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

    override fun onNoOtherActionHandle(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ) {
        super.onNoOtherActionHandle(action, service, event)

        //无法识别的界面, 执行back操作

        if (KSShareAction().checkEvent(service, event) ||
            KSLikeAction().checkEvent(service, event) ||
            KSCommentSendAction().checkEvent(service, event)
        ) {
            service.back()
        } else {
            //低端机, 可能点击了没效果的情况, 这里弥补一下
            KSLoginAction().apply {
                if (checkEvent(service, event)) {
                    doAction(service, event)
                }
            }
        }
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