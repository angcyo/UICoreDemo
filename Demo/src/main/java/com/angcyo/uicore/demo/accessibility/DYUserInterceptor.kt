package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.action.ActionException
import com.angcyo.core.component.accessibility.isActionFinish
import com.angcyo.core.component.accessibility.openApp
import com.angcyo.core.vmCore

/**
 * 获取抖音登录用户名拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/01
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class DYUserInterceptor : BaseDYInterceptor() {

    init {
        actionList.add(DYLoginAction())
    }

    fun sendNotify(content: String) {
        sendNotify(
            "抖音用户数据获取[${DYLikeInterceptor.dyUserName}]($actionIndex/${actionList.size})",
            content
        )
    }

    override fun onDoAction(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService?,
        event: AccessibilityEvent?
    ) {
        sendNotify("正在执行:${action.getActionTitle()}")
        super.onDoAction(action, service, event)
    }

//    override fun onNoOtherActionHandle(
//        action: BaseAccessibilityAction,
//        service: BaseAccessibilityService,
//        event: AccessibilityEvent?
//    ) {
//        super.onNoOtherActionHandle(action, service, event)
//
//        if (actionIndex < 1) {
//            //无法识别的界面, 执行back操作
//            service.back()
//        }
//    }

    override fun onActionFinish(error: ActionException?) {
        if (actionStatus.isActionFinish()) {
            openApp()
            //DslNotify.cancelNotify(lastService, notifyId)

            vmCore<DYModel>().userNameData.value?.let {
                sendNotify(null, "抖音号:${it}")
            }
        }
        super.onActionFinish(error)
    }
}