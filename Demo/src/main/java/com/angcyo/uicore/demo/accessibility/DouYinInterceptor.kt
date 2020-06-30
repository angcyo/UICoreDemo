package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.component.accessibility.AccessibilityHelper.logFolderName
import com.angcyo.core.component.file.DslFileHelper
import com.angcyo.core.component.file.wrapData
import com.angcyo.library.L
import com.angcyo.library.component.dslNotify
import com.angcyo.library.component.low
import com.angcyo.library.component.single
import com.angcyo.library.tip
import com.angcyo.uicore.demo.R

/**
 * 抖音 复制口令->打开看看->点赞
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DouYinInterceptor : BaseAccessibilityInterceptor() {

    companion object {

        const val DY_PACKAGE_NAME = "com.ss.android.ugc.aweme"

        fun log(data: String) {
            DslFileHelper.write(logFolderName, "dy.log", data.wrapData())
        }
    }

    val dyNotifyId = 101

    init {
        filterPackageNameList.add(DY_PACKAGE_NAME)
        actionList.add(DYShareAction())
        actionList.add(DYLikeAction())

        actionOtherList.add(DYPrivacyAction())
        actionOtherList.add(DYPermissionsAction())
        actionOtherList.add(DYProtectAction())
        actionOtherList.add(DYGuideAction())
        actionOtherList.add(DYUpdateAction())
        actionOtherList.add(DYContactsAction())
        actionOtherList.add(DYShareAction())

        ignoreInterceptor = true
        enableInterval = true
        intervalDelay = 4_000
    }

    fun sendNotify(content: String) {
        dslNotify {
            notifyId = dyNotifyId
            notifyOngoing = actionStatus.isActionStart()
            low()
            single("抖音点赞任务($actionIndex/${actionList.size})", content)
        }
    }

    override fun onAccessibilityEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ) {
        super.onAccessibilityEvent(service, event)

        //L.v(event.source?.log())

//        if (event.isWindowStateChanged()) {
        // L.i(service.rootInActiveWindow)
        // L.w(service.windows)
        // service.windows.forEach {
        //     L.e(it.root)
        //     L.v(it.root?.debugNodeInfo())
        //     //L.v(it.root?.log())
        // }
//        }

//        if (event.isWindowContentChanged()) {
        //service.rootNodeInfo(event)?.logNodeInfo()
//        }
    }

    override fun onNoOtherActionHandle(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ) {
        super.onNoOtherActionHandle(service, event)
    }

    override fun onDoAction(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService?,
        event: AccessibilityEvent?
    ) {
        sendNotify("正在执行:${action.getActionTitle()}")
        super.onDoAction(action, service, event)
    }

    override fun onActionFinish() {
        L.w("执行结束:$actionStatus")
        if (actionStatus == ACTION_STATUS_ERROR) {
            //出现异常
            tip("执行异常!", R.drawable.lib_ic_error)
            sendNotify("执行异常!")
        } else if (actionStatus == ACTION_STATUS_FINISH) {
            //流程结束
            tip("执行完成!")
            sendNotify("执行完成!")
            lastService?.home()
        }
    }
}