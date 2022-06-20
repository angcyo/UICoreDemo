package com.angcyo.uicore.dslitem

import androidx.annotation.DrawableRes
import com.angcyo.coroutine.launchGlobal
import com.angcyo.coroutine.onBack
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.glide.clear
import com.angcyo.glide.gifOfRes
import com.angcyo.glide.giv
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/29
 */
class AppResImageItem : AppImageItem(1) {

    @DrawableRes
    var itemImageRes: Int = -1

    init {
        imageUrl = null
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.giv(R.id.lib_image_view)?.apply {
            clear()
            clearOverlay()
            //dslGlide._glide().load(itemImageRes).into(this)

            launchGlobal {
                onBack { gifOfRes(resources, itemImageRes) }.await()?.run {
                    setImageDrawable(this)
                }
            }
        }
    }
}