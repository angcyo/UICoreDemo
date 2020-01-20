package com.angcyo.uicore.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.glide.GlideImageView
import com.angcyo.glide.giv
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.setHeight

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/20
 */
class AppImageItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.app_item_image
    }

    var imageUrl: String? = null

    var imageHeight = -1

    var onConfigGlideImage: (GlideImageView) -> Unit = {}

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        itemHolder.itemView.setHeight(imageHeight)

        itemHolder.giv(R.id.image_view)?.apply {
            reset()
            onConfigGlideImage(this)
            url = imageUrl
        }
    }
}