package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.item.DslBaseInfoItem
import com.angcyo.library.ex.dpi
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
                    val type = when {
                        i % 2 == 0 -> 1
                        i % 3 == 0 -> 2
                        else -> 0
                    }

                    itemInfoText = "Text...$i" + when (type) {
                        1 -> " _activity"
                        2 -> " _qq"
                        else -> " _normal"
                    }

                    itemTopInsert = 1 * dpi

                    onItemClick = {

                        when (type) {
                            1 -> toast(itemInfoText, R.drawable.lib_ic_info) {
                                withActivity = getActivity()
                            }
                            2 -> toast(
                                itemInfoText,
                                R.drawable.lib_ic_succeed,
                                R.layout.lib_qq_toast_layout
                            )
                            else -> toast(itemInfoText, R.drawable.lib_ic_waring)
                        }
                    }
                }
            }
        }
    }
}