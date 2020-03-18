package com.angcyo.uicore.demo

import com.angcyo.dsladapter.itemIndexPosition
import com.angcyo.github.dslitem.DslBannerItem
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppImageItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/18
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class PagerLayoutManagerDemo : AppDslFragment() {
    override fun onInitFragment() {
        super.onInitFragment()
        renderDslAdapter {
            DslBannerItem()() {
                itemBannerAdapter.apply {
                    for (i in 0..10) {
                        AppImageItem(i)() {
                            imageText = "Position $i"
                            imageHeight = -1
                            onItemClick = {
                                toastQQ("click ${itemIndexPosition()}")
                            }
                        }
                    }
                }
            }
            DslBannerItem()()
            DslBannerItem()()

            for (i in 0..10) {
                AppImageItem(i)() {
                    imageText = "Position $i"
                    imageHeight = -1
                    onItemClick = {
                        toastQQ("click ${itemIndexPosition()}")
                    }
                }
            }
        }
    }
}