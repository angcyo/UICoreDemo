package com.angcyo.uicore.demo.accessibility

import androidx.lifecycle.MutableLiveData
import com.angcyo.uicore.LifecycleViewModel

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/30
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYModel : LifecycleViewModel() {

    //抖音登录状态, 是否登录
    val loginData: MutableLiveData<Boolean> = MutableLiveData(true)

    //抖音登录用户名
    val userNameData: MutableLiveData<CharSequence?> = MutableLiveData(null)
}