package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.undefined_size
import com.angcyo.uicore.base.AppFragment
import com.angcyo.uicore.dslitem.loadImageItem
import com.angcyo.uicore.dslitem.loadTextItem
import com.angcyo.widget.recycler.initDslAdapter

/**
 *
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
        _vh.rv(R.id.recycler_header_view)?.initDslAdapter {
            loadTextItem(4)
        }

        _vh.rv(R.id.recycler_footer_view)?.initDslAdapter {
            loadImageItem(22) {
                imageHeight = undefined_size
                itemHeight = 100 * dpi
                imageUrl = null
            }
        }
    }
}