package com.angcyo.uicore.demo

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.angcyo.core.dslitem.IFragmentItem
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.pager.dslSinglePager
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class PreviewBitmapItem : DslAdapterItem(), IFragmentItem {

    var bitmap: Bitmap? = null

    override var itemFragment: Fragment? = null

    init {
        itemLayoutId = R.layout.item_preview_bitmap_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.img(R.id.image_view)?.setImageBitmap(bitmap)

        itemHolder.clickItem {
            itemFragment?.dslSinglePager(itemHolder.img(R.id.image_view), bitmap)
        }
    }
}