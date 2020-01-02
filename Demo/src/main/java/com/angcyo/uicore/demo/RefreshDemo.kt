package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.fragment.BaseTitleFragment
import com.angcyo.core.fragment.initDslAdapter
import com.angcyo.drawable.dpi
import com.angcyo.item.DslBaseInfoItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class RefreshDemo : BaseTitleFragment() {
    override fun getContentLayoutId() = R.layout.demo_refresh

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.rv(R.id.lib_recycler_view)?.initDslAdapter {
            for (i in 0..100) {
                DslBaseInfoItem()() {
                    itemInfoText = "Text...$i"
                    itemTopInsert = 1 * dpi
                }
            }
        }
    }
}