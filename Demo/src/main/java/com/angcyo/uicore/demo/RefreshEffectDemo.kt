package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.item.DslBaseInfoItem
import com.angcyo.library.ex.dpi
import com.angcyo.library.toast
import com.angcyo.library.toastQQ
import com.angcyo.library.toastWX
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
                    val type = i % 4
                    val withAcy = i % 3 == 0

                    itemInfoText = "Text...$i" + when (type) {
                        1 -> " _activity"
                        2 -> " _qq"
                        3 -> " _wx"
                        else -> " _normal"
                    } + if (withAcy) " _widthActivity" else " _toast"

                    itemTopInsert = 1 * dpi

                    itemClick = {
                        when (type) {
                            1 -> toast(itemInfoText, R.drawable.lib_ic_info) {
                                withActivity = if (withAcy) activity else null
                            }
                            2 -> toastQQ(itemInfoText, R.drawable.lib_ic_succeed) {
                                withActivity = if (withAcy) activity else null
                            }
                            3 -> toastWX(itemInfoText, R.drawable.lib_ic_error) {
                                withActivity = if (withAcy) activity else null
                            }
                            else -> toast(itemInfoText, R.drawable.lib_ic_waring) {
                                withActivity = if (withAcy) activity else null
                            }
                        }
                    }
                }
            }
        }
    }
}