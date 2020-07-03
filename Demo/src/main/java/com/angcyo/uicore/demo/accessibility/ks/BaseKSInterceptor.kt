package com.angcyo.uicore.demo.accessibility.ks

import com.angcyo.uicore.demo.accessibility.BaseFloatInterceptor

/**
 * 快手基类拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseKSInterceptor : BaseFloatInterceptor() {
    init {
        filterPackageNameList.add(KSLikeInterceptor.KS_PACKAGE_NAME)

        actionOtherList.add(KSPrivacyAction())
        actionOtherList.add(KSGetUserAction())
    }
}