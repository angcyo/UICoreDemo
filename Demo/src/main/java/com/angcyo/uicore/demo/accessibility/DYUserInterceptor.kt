package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.component.accessibility.action.ActionException
import com.angcyo.library.component.DslNotify
import com.angcyo.library.ex.openApp

/**
 * 获取抖音登录用户名拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/01
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class DYUserInterceptor : BaseAccessibilityInterceptor() {

    init {
        filterPackageNameList.add(DYLikeInterceptor.DY_PACKAGE_NAME)
        actionList.add(DYLoginAction())

        intervalMode()
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
            lastService?.openApp(lastService?.packageName)
            DslNotify.cancelNotify(lastService, notifyId)
        }
        super.onActionFinish(error)
    }
}