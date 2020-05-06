package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.View
import com.angcyo.base.delay
import com.angcyo.behavior.linkage.LinkageHeaderBehavior
import com.angcyo.core.viewpager.ViewPager1Delegate
import com.angcyo.library.L
import com.angcyo.library.ex.fullTime
import com.angcyo.library.ex.nowTime
import com.angcyo.uicore.base.AppFragment
import com.angcyo.uicore.dslitem.loadTextItem
import com.angcyo.uicore.initAdapter
import com.angcyo.widget.base.behavior
import com.angcyo.widget.recycler.initDslAdapter
import com.angcyo.widget.vp

/**
 * RecyclerView + TabLayout + ViewPager
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/21
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class LinkageVpBehaviorDemo : AppFragment() {
    init {
        fragmentLayoutId = R.layout.demo_linkage_vp_behavior
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        delay {
            _vh.rv(R.id.recycler_header_view)?.initDslAdapter {
                loadTextItem()
            }

            _vh.vp(R.id.lib_view_pager)?.apply {
                initAdapter(childFragmentManager)
                ViewPager1Delegate.install(this, _vh.v(R.id.lib_tab_layout))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _vh.view(R.id.header_wrap_layout).behavior()?.apply {
            if (this is LinkageHeaderBehavior) {
                refreshAction = {
                    L.i("收到刷新回调...${nowTime().fullTime()}")
                    _vh.postDelay(2_000) {
                        L.i("请求结束刷新!")
                        finishRefresh()
                    }
                }
            }
        }
    }

    override fun canFlingBack(): Boolean {
        return false
    }
}