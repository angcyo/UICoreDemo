package com.angcyo.uicore.demo

import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.itemIndexPosition
import com.angcyo.github.dslitem.DslBannerItem
import com.angcyo.item.DslNestedRecyclerItem
import com.angcyo.item.style.itemNestedAdapter
import com.angcyo.item.style.itemNestedLayoutManager
import com.angcyo.library._screenWidth
import com.angcyo.library.ex.dpi
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppImageItem
import com.angcyo.widget.recycler.LinearLayoutManagerWrap
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
            DslBannerItem()() {
                itemNestedAdapter.data()
            }

            DslBannerItem()() {
                val width = _screenWidth - 200 * dpi
                itemNestedAdapter.data(width)
                (pagerLayoutManager as? ScaleLayoutManager)?.apply {
                    itemSpace = -width / 3
                    minScale = 0.6f
                    isFullItem = false
                }
            }

            DslBannerItem()() {
                val width = _screenWidth - 200 * dpi
                itemNestedAdapter.data(width)
                (pagerLayoutManager as? ScaleLayoutManager)?.apply {
                    itemSpace = -width / 3
                    minScale = 0.6f
                    isFullItem = false
                    forceSpaceMain = 0
                }
            }

            DslNestedRecyclerItem()() {
                itemNestedLayoutManager =
                    LinearLayoutManagerWrap(fContext(), RecyclerView.HORIZONTAL)
                itemNestedAdapter.apply {
                    for (i in 0..10) {
                        AppImageItem(i)() {
                            itemWidth = 100 * dpi
                            itemHeight = 100 * dpi
                            imageText = "Position $i"
                            imageHeight = -1
                            itemClick = {
                                toastQQ("click ${itemIndexPosition()}")
                            }
                        }
                    }
                }
            }

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