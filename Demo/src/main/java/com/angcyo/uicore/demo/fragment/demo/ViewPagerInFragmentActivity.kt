package com.angcyo.uicore.demo.fragment.demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.base.dslFHelper
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.demo.fragment.Fragment1
import com.angcyo.uicore.demo.fragment.Fragment2
import com.angcyo.uicore.demo.fragment.Fragment3
import com.angcyo.widget.DslViewHolder

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
    override fun getFragmentLayoutId(): Int = R.layout.fragment_view_pager_layout

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val fragments = listOf(Fragment1(), Fragment2(), Fragment3())
        baseViewHolder.v<ViewPager>(R.id.view_pager)?.adapter = object :
            FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }
        }
    }
}