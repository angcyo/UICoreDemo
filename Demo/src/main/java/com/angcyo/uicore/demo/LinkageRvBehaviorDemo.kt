package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.base.delay
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.undefined_size
import com.angcyo.uicore.base.AppFragment
import com.angcyo.uicore.dslitem.loadImageItem
import com.angcyo.uicore.dslitem.loadTextItem
import com.angcyo.widget.recycler.initDslAdapter

/**
 * RecyclerView + RecyclerView
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/20
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class LinkageRvBehaviorDemo : AppFragment() {
    init {
        fragmentLayoutId = R.layout.demo_linkage_recycler_behavior
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        delay {
            _vh.rv(R.id.recycler_header_view)?.initDslAdapter {
                loadTextItem()
            }
            _vh.rv(R.id.recycler_footer_view)?.initDslAdapter {
                loadImageItem(1) {
                    imageHeight = undefined_size
                    itemHeight = 100 * dpi
                    imageUrl = null
                }
                loadTextItem()
            }
        }
    }
}