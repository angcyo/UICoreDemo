package com.angcyo.uicore.fragment

import android.os.Bundle
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.undefined_size
import com.angcyo.uicore.base.AppFragment
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.dslitem.loadImageItem
import com.angcyo.uicore.dslitem.loadTextItem
import com.angcyo.widget.recycler.initDslAdapter

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/21
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RecyclerTextFragment : AppFragment() {
    init {
        fragmentLayoutId = R.layout.layout_recycler_view
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        _vh.rv(R.id.lib_recycler_view)?.initDslAdapter {
            loadImageItem(1) {
                imageHeight = undefined_size
                itemHeight = 100 * dpi
                imageUrl = null
            }
            loadTextItem()
            loadImageItem(1) {
                imageHeight = undefined_size
                itemHeight = 100 * dpi
                imageUrl = null
            }
        }
    }
}