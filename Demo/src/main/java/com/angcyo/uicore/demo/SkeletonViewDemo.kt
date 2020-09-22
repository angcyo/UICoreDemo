package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/09/22
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class SkeletonViewDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            dslItem(R.layout.demo_skeleton_layout)
        }
    }
}