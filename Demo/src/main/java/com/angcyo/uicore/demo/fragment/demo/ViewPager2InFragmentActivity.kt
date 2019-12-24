package com.angcyo.uicore.demo.fragment.demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
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
class ViewPager2InFragmentActivity : BaseAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            show(FragmentViewPager2())
        }
    }
}

class FragmentViewPager2 : AbsLifecycleFragment() {
    override fun getFragmentLayoutId(): Int = R.layout.fragment_view_pager2_layout


    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val fragments = listOf(Fragment1(), Fragment2(), Fragment3())
        baseViewHolder.v<ViewPager2>(R.id.view_pager)?.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

        }
    }
}