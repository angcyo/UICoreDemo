package com.angcyo.uicore.test

import com.angcyo.core.activity.BasePermissionsActivity
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.demo.R
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/17
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class TestActivity : BasePermissionsActivity() {

    init {
        activityLayoutId = R.layout.activity_test
    }

    override fun onPermissionGranted() {
        _vh.tv(R.id.lib_text_view)?.text = span {
            append("TestActivity->")
            append(nowTimeString())
            appendln()
            append(intent?.toString())
            appendln()
            append(intent?.extras?.toString())
        }
    }
}