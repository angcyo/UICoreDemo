package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.component.accessibility.action.ActionException
import com.angcyo.core.component.file.DslFileHelper
import com.angcyo.core.component.file.wrapData
import com.angcyo.core.vmCore
import com.angcyo.uicore.demo.accessibility.ks.BaseKSInterceptor

/**
 * 快手点赞拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/02
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSLikeInterceptor : BaseKSInterceptor() {
    companion object {

        const val KS_PACKAGE_NAME = "com.smile.gifmaker"

        val ksUserName: CharSequence get() = vmCore<KSModel>().userNameData.value ?: "未登录"

        fun log(data: String) {
            DslFileHelper.write(
                AccessibilityHelper.logFolderName,
                "ks.log",
                "$ksUserName:$data".wrapData()
            )
        }
    }

    init {
        intervalMode()

        actionList.add(KSShareAction())
        actionList.add(KSLikeAction())
        actionList.add(KSCommentAction())
        actionList.add(KSCommentSendAction())
        actionList.add(KSLikeFinishAction())
    }


    override fun onActionStart() {
        super.onActionStart()
        sendNotify("快手任务[${ksUserName}]", "就绪")
    }

    override fun checkDoAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        if (vmCore<KSModel>().isLogin()) {
            super.checkDoAction(service, event)
        } else {
            sendNotify(null, "请先登录快手")

            //检查抖音是否已登录
            KSLoginAction().apply {
                actionFinish = {
                    KSLoginAction().doAction(service, event)
                }
                doAction(service, event)
            }
        }
    }

    override fun onDoAction(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService?,
        event: AccessibilityEvent?
    ) {
        sendNotify(
            "快手点赞任务[$ksUserName]($actionIndex/${actionList.size})",
            "正在执行:${action.actionTitle}"
        )
        super.onDoAction(action, service, event)
    }

    override fun onActionFinish(action: BaseAccessibilityAction?, error: ActionException?) {
        log("执行结束:$actionStatus")
        if (actionStatus == ACTION_STATUS_ERROR) {
            //出现异常
            sendNotify("快手点赞任务[$ksUserName]", "执行异常,${error?.message}.")
        } else if (actionStatus == ACTION_STATUS_FINISH) {
            //流程结束
            sendNotify("快手点赞任务[$ksUserName]", "执行完成!")
            //lastService?.home()
            openApp()
        }
        super.onActionFinish(action, error)
    }
}