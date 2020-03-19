package com.angcyo.uicore.demo

import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.itemIndexPosition
import com.angcyo.github.dslitem.DslBannerItem
import com.angcyo.library.ex.dpi
import com.angcyo.library.getScreenWidth
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppImageItem
import com.leochuan.ScaleLayoutManager

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

        fun DslAdapter.data(width: Int = -1) {
            this.apply {
                for (i in 0..10) {
                    AppImageItem(i)() {
                        itemWidth = width
                        imageText = "Position $i"
                        imageHeight = -1
                        itemClick = {
                            toastQQ("click ${itemIndexPosition()}")
                        }
                    }
                }
            }
        }

        renderDslAdapter {
//            DslBannerItem()() {
//                itemBannerAdapter.data()
//            }
            DslBannerItem()() {
                itemBannerAdapter.data(getScreenWidth() - 200 * dpi)
                (itemBannerLayoutManager as? ScaleLayoutManager)?.apply {
                    itemSpace = -100 * dpi
                    minScale = 0.6f
                    isFullItem = false
                }
            }

//            DslBannerItem()() {
//                itemBannerAdapter.data()
//            }

            for (i in 0..10) {
                AppImageItem(i)() {
                    imageText = "Position $i"
                    imageHeight = -1
                    itemClick = {
                        toastQQ("click ${itemIndexPosition()}")
                    }
                }
            }
        }
    }
}