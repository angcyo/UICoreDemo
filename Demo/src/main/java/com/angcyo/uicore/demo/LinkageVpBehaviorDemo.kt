package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.uicore.base.AppFragment
import com.angcyo.uicore.dslitem.loadTextItem
import com.angcyo.uicore.initAdapter
import com.angcyo.widget.recycler.initDslAdapter
import com.angcyo.widget.vp

/**
 *
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

        _vh.rv(R.id.recycler_header_view)?.initDslAdapter {
            loadTextItem()
        }

        _vh.vp(R.id.lib_view_pager)?.initAdapter(childFragmentManager)
    }

    override fun canFlingBack(): Boolean {
        return false
    }
}