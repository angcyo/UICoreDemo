package com.angcyo.uicore.activity

import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.base.dslFHelper
import com.angcyo.core.viewpager.RFragmentAdapter
import com.angcyo.core.viewpager.ViewPager1Delegate
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.fragment.Fragment1
import com.angcyo.uicore.fragment.Fragment2
import com.angcyo.uicore.fragment.Fragment3
import com.angcyo.widget.tab
import com.angcyo.widget.vp

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ViewPagerInFragmentActivity : BaseAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            show(FragmentViewPager())
        }
    }
}

class FragmentViewPager : AbsLifecycleFragment() {

    init {
        fragmentLayoutId = R.layout.fragment_view_pager_layout
    }

    val fragments = mutableListOf(Fragment1(), Fragment2(), Fragment3())

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        baseViewHolder.vp(R.id.view_pager)?.apply {
            adapter = RFragmentAdapter(childFragmentManager, fragments)

            ViewPager1Delegate.install(this, baseViewHolder.tab(R.id.tab_layout))
        }
    }
}