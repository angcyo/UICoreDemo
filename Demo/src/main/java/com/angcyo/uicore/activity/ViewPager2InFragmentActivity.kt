package com.angcyo.uicore.activity

import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.base.dslFHelper
import com.angcyo.core.viewpager2.RFragmentAdapter2
import com.angcyo.core.viewpager2.ViewPager2Delegate
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.fragment.Fragment1
import com.angcyo.uicore.fragment.Fragment2
import com.angcyo.uicore.fragment.Fragment3
import com.angcyo.widget.tab
import com.angcyo.widget.vp2

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ViewPager2InFragmentActivity : BaseAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            show(FragmentViewPager2())
        }
    }
}

class FragmentViewPager2 : AbsLifecycleFragment() {

    init {
        fragmentLayoutId = R.layout.fragment_view_pager2_layout
    }

    val fragments = mutableListOf(Fragment1(), Fragment2(), Fragment3())

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.vp2(R.id.view_pager)?.apply {
            adapter = RFragmentAdapter2(this@FragmentViewPager2, fragments)

            ViewPager2Delegate.install(this, _vh.tab(R.id.tab_layout))
        }
    }
}