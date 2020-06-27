package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.component.file.DslFileHelper
import com.angcyo.core.component.file.wrapData

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
            DslFileHelper.write("accessibility", "dy.log", data.wrapData())
        }
    }

    init {
        filterPackageNameList.add(DY_PACKAGE_NAME)
        actionList.add(DYShareAction())
        actionList.add(DYLikeAction())

        actionOtherList.add(DYPrivacyAction())
        actionOtherList.add(DYPermissionsAction())
        actionOtherList.add(DYProtectAction())
        actionOtherList.add(DYGuideAction())
        actionOtherList.add(DYUpdateAction())
    }

    override fun onAccessibilityEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent
    ) {
        super.onAccessibilityEvent(service, event)

        //L.v(event.source?.log())

        if (event.isWindowStateChanged()) {
            // L.i(service.rootInActiveWindow)
            // L.w(service.windows)
            // service.windows.forEach {
            //     L.e(it.root)
            //     L.v(it.root?.debugNodeInfo())
            //     //L.v(it.root?.log())
            // }
        }

        if (event.isWindowContentChanged()) {
            //service.rootNodeInfo(event)?.logNodeInfo()
        }
    }

    override fun onNoOtherActionHandle(
        service: BaseAccessibilityService,
        event: AccessibilityEvent
    ) {
        super.onNoOtherActionHandle(service, event)

        service.rootNodeInfo(event)?.logNodeInfo()
    }
}