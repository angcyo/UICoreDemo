package com.angcyo.uicore.demo.accessibility.dy

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.AccessibilityHelper.logFolderName
import com.angcyo.core.component.accessibility.BaseAccessibilityAction
import com.angcyo.core.component.accessibility.BaseAccessibilityService
import com.angcyo.core.component.accessibility.action.ActionException
import com.angcyo.core.component.accessibility.openApp
import com.angcyo.core.component.file.DslFileHelper
import com.angcyo.core.component.file.wrapData
import com.angcyo.core.vmCore
import com.angcyo.library.L

/**
 * 抖音 复制口令->打开看看->点赞->评论->分享->结束
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYLikeInterceptor : BaseDYInterceptor() {

    companion object {

        const val DY_PACKAGE_NAME = "com.ss.android.ugc.aweme"

        val dyUserName: CharSequence get() = vmCore<DYModel>().userNameData.value ?: "未登录"

        fun log(data: String) {
            DslFileHelper.write(logFolderName, "dy.log", "$dyUserName:$data".wrapData())
        }
    }

    val loginAction = DYLoginAction()

    init {
        actionList.add(DYShareAction())
        actionList.add(DYLikeAction())
        actionList.add(DYCommentAction())
        actionList.add(DYCommentSendAction())
        actionList.add(DYLikeFinishAction())
    }

    fun sendNotify(content: String) {
        sendNotify("抖音点赞任务[$dyUserName]($actionIndex/${actionList.size})", content)
    }

    override fun checkDoAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        if (vmCore<DYModel>().loginData.value == false) {
            sendNotify("请先登录抖音.")

            //检查抖音是否已登录
            loginAction.doAction(service, event)
        } else {
            super.checkDoAction(service, event)
        }
    }

    override fun onDoAction(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService?,
        event: AccessibilityEvent?
    ) {
        sendNotify("正在执行:${action.getActionTitle()}")
        super.onDoAction(action, service, event)
    }

    override fun onActionFinish(error: ActionException?) {
        L.w("执行结束:$actionStatus")
        if (actionStatus == ACTION_STATUS_ERROR) {
            //出现异常
            sendNotify("执行异常,${error?.message}.")
        } else if (actionStatus == ACTION_STATUS_FINISH) {
            //流程结束
            sendNotify("执行完成!")
            //lastService?.home()
            openApp()
        }
        super.onActionFinish(error)
    }
}