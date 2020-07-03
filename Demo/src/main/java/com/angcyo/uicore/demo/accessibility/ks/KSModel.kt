package com.angcyo.uicore.demo.accessibility.ks

import androidx.lifecycle.MutableLiveData
import com.angcyo.uicore.LifecycleViewModel

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020-7-3
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class KSModel : LifecycleViewModel() {

    //快手登录状态, 是否登录
    val loginData: MutableLiveData<Boolean> = MutableLiveData(false)

    //快手登录用户名
    val userNameData: MutableLiveData<CharSequence?> = MutableLiveData(null)

    /**快手是否已登录*/
    fun isLogin() = !userNameData.value.isNullOrEmpty() || loginData.value == true

    /**设置登录用户名*/
    fun login(name: CharSequence?) {
        userNameData.value = name
        if (name.isNullOrEmpty()) {
            loginData.postValue(false)
        } else {
            loginData.postValue(true)
        }
    }
}