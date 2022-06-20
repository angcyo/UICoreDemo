package com.angcyo.uicore.dslitem

import com.angcyo.base.dslAHelper
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.copy
import com.angcyo.library.ex.longFeedback
import com.angcyo.library.ex.or
import com.angcyo.library.model.AppBean
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.bumptech.glide.Glide

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class AppItem : DslAdapterItem() {
    var appBean: AppBean? = null

    init {
        itemLayoutId = R.layout.app_item_app_layout

        itemClick = {
            appBean?.run {
                it.context.dslAHelper {
                    start(packageName)
                }
            }
        }

        itemLongClick = {
            appBean?.run {
                toString().copy()
                it.longFeedback()
            }
            true
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        appBean?.run {
            itemHolder.img(R.id.image_view)?.also {
                Glide.with(it).load(appIcon).into(it)
            }
        }
        itemHolder.tv(R.id.text_view)?.text = appBean?.appName.or()
    }
}