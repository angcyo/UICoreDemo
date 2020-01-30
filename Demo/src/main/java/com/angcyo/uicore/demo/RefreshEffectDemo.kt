package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.library.ex.dpi
import com.angcyo.item.DslBaseInfoItem
import com.angcyo.library.toast
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.recycler.initDslAdapter

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class RefreshEffectDemo : AppTitleFragment() {

    init {
        contentLayoutId = R.layout.demo_refresh
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.rv(R.id.lib_recycler_view)?.initDslAdapter {
            for (i in 0..100) {
                DslBaseInfoItem()() {
                    itemInfoText = "Text...$i"
                    itemTopInsert = 1 * dpi

                    onItemClick = {
                        if (i % 2 == 0) {
                            toast(itemInfoText)
                        } else {
                            toast(itemInfoText) {
                                activity = getActivity()
                            }
                        }
                    }
                }
            }
        }
    }
}