package com.angcyo.uicore.dslitem

import android.view.Gravity
import com.angcyo.drawable.toDpi
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.glide.GlideImageView
import com.angcyo.glide.giv
import com.angcyo.http.OkType
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
            clearOverlay()
            onConfigGlideImage(this)

            val url = imageUrl
            //"http://5b0988e595225.cdn.sohucs.com/images/20171227/157724ff25b9415e8853050a58e4a581.gif"

            load(url) {
                onTypeCallback = {
                    if (it == OkType.ImageType.GIF) {
                        addOverlayDrawable(
                            com.angcyo.drawable.getDrawable(R.drawable.gif),
                            Gravity.RIGHT or Gravity.BOTTOM,
                            6.toDpi(), 6.toDpi()
                        )
                    }
                }
                checkGifType = url?.endsWith("gif") ?: false
            }
        }
    }
}