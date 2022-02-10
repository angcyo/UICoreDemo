package com.angcyo.uicore.demo.ipc

import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.uicore.demo.BinderDemo

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/10
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class IpcActivity2 : BaseCoreAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)
        dslFHelper {
            restore(BinderDemo::class)
        }
    }

}