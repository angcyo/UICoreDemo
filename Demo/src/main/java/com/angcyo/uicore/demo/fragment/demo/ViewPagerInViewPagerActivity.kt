package com.angcyo.uicore.demo.fragment.demo

import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.base.dslFHelper
import com.angcyo.uicore.demo.fragment.Fragment2
import com.angcyo.uicore.demo.fragment.Fragment3

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ViewPagerInViewPagerActivity : BaseAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            show(FragmentViewPager().apply {
                fragments.clear()
                fragments.add(FragmentViewPager())
                fragments.add(Fragment2())
                fragments.add(Fragment3())
            })
        }
    }
}