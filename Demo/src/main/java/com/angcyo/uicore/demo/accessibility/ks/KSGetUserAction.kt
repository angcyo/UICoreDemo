package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.vmCore

/**
 * 快手获取用户账号
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSGetUserAction : BaseAccessibilityAction() {

    //头像
    fun AccessibilityNodeInfoCompat.avatarNode(): AccessibilityNodeInfoCompat? {
        if (haveText("头像") && isImageView()) {
            return this
        }
        return null
    }

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var haveDynamic = false
        var haveMessage = false
        var haveInbox = false
        var haveSetting = false

        service.findNode {
            if (it.haveText("动态") && it.isValid()) {
                haveDynamic = true
            }
            if (it.haveText("消息") && it.isValid()) {
                haveMessage = true
            }
            if (it.haveText("私信") && it.isValid()) {
                haveInbox = true
            }
            if (it.haveText("设置") && it.isValid()) {
                haveSetting = true
            }
        }

        return haveDynamic && haveMessage && haveInbox && haveSetting
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)

        var nameNode: AccessibilityNodeInfoCompat? =
            service.findNodeById("com.smile.gifmaker:id/tab_name").firstOrNull()?.wrap()

        if (nameNode != null) {
            //获取快手账号
            nameNode.apply {
                val name: String? = text?.toString()
                vmCore<KSModel>().login(text)

                name?.let {
                    KSLikeInterceptor.log("获取到快手账号:[$it]")
                    service.back()
                    onActionFinish()
                }
            }
            return
        }

        //用头像node定位
        var avatarNode: AccessibilityNodeInfoCompat? = null

        service.findNode {
            avatarNode = avatarNode ?: it.avatarNode()
        }

        //昵称
        nameNode = avatarNode?.getBrotherNode(1)?.getChildOrNull(0)

        nameNode?.apply {
            //获取快手账号
            val name: String? = text?.toString()
            vmCore<KSModel>().login(text)

            name?.let {
                KSLikeInterceptor.log("获取到快手账号:[$it]")

                service.back()

                onActionFinish()
            }
        }
    }

    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        if (checkEvent(service, event)) {
            return service.back()
        }
        return super.doActionWidth(action, service, event)
    }

    override fun getActionTitle(): String {
        return "读取快手账号"
    }
}