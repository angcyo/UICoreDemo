package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.behavior.linkage.LinkageHeaderBehavior
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.undefined_size
import com.angcyo.uicore.base.AppFragment
import com.angcyo.uicore.dslitem.loadImageItem
import com.angcyo.uicore.dslitem.loadTextItem
import com.angcyo.widget.base.behavior
import com.angcyo.widget.recycler.initDslAdapter

/**
 * NestedScrollView + RecyclerView
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/20
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class LinkageSingleBehaviorDemo : AppFragment() {
    init {
        fragmentLayoutId = R.layout.demo_linkage_behavior
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        _vh.rv(R.id.recycler_footer_view)?.initDslAdapter {
            loadImageItem(1) {
                imageHeight = undefined_size
                itemHeight = 100 * dpi
                imageUrl = null
            }
            loadTextItem()
        }

        _vh.click(R.id.open_button) {
            (_vh.view(R.id.header_wrap_layout).behavior() as? LinkageHeaderBehavior)?.open()
        }

        _vh.click(R.id.close_button) {
            (_vh.view(R.id.header_wrap_layout).behavior() as? LinkageHeaderBehavior)?.close()
        }
    }
}