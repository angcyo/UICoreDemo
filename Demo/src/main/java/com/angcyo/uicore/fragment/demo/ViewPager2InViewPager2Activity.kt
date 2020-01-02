package com.angcyo.uicore.fragment.demo

import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.base.dslFHelper
import com.angcyo.uicore.fragment.Fragment2
import com.angcyo.uicore.fragment.Fragment3

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class ViewPager2InViewPager2Activity : BaseAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            show(FragmentViewPager2().apply {
                fragments.clear()
                fragments.add(FragmentViewPager2())
                fragments.add(Fragment2())
                fragments.add(Fragment3())
            })
        }
    }
}