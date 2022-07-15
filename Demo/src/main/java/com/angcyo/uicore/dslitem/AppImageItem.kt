package com.angcyo.uicore.dslitem

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.github.dslitem.ILoopAdapterItem
import com.angcyo.glide.GlideImageView
import com.angcyo.glide.giv
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.utils.ImageTypeUtil
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.layout.RFrameLayout

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/20
 */
open class AppImageItem(index: Int = -1) : DslAdapterItem(), ILoopAdapterItem {
    var imageUrl: String? = null

    var imageHeight = 0

    var imageMask: Boolean = false

    var imageText: CharSequence? = null

    var onConfigGlideImage: (GlideImageView) -> Unit = {}

    init {
        itemLayoutId = R.layout.app_item_image

        imageUrl = image()

        if (index >= 0) {
            imageHeight = if (index % 2 == 0) 280 * dpi else 240 * dpi
        }

        onConfigGlideImage = {
            it.dslGlide.placeholderDrawable = ColorDrawable(randomColorAlpha())
            it.dslGlide.errorDrawable = ColorDrawable(randomColorAlpha())

            it.maskDrawable = when {
                !imageMask -> null
                index % 5 == 0 -> _drawable(R.drawable.ic_logo)
                index % 4 == 0 -> _drawable(R.drawable.qipao_guosheng)
                index % 3 == 0 -> _drawable(R.drawable.qipao1)
                index % 2 == 0 -> _drawable(R.drawable.qipao)
                else -> null
            }
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        if (imageHeight == 0) {
            //等宽
            if (itemHolder.itemView is RFrameLayout) {
                (itemHolder.itemView as RFrameLayout).layoutDelegate.layoutDimensionRatio = "1"
            }
        } else if (imageHeight > 0) {
            itemHolder.itemView.setHeight(imageHeight)
        }

        itemHolder.tv(R.id.lib_text_view)?.text = imageText

        itemHolder.giv(R.id.lib_image_view)?.apply {
            clearOverlay()
            onConfigGlideImage(this)

            val url = imageUrl
            //"http://5b0988e595225.cdn.sohucs.com/images/20171227/157724ff25b9415e8853050a58e4a581.gif"

            load(url) {
                onTypeCallback = {
                    if (it == ImageTypeUtil.ImageType.GIF) {
                        addOverlayDrawable(
                            _drawable(R.drawable.gif),
                            Gravity.RIGHT or Gravity.BOTTOM,
                            6.toDpi(), 6.toDpi()
                        )
                    }
                }
//                overrideWidth = 100
//                overrideHeight = 100
                checkGifType = url?.endsWith("gif") ?: false
                transition = true

                if (itemPosition % 4 == 0) {
                    blur()
                } else if (itemPosition % 6 == 0) {
                    grayscale()
                }
            }
            setOnClickListener(_clickListener)
        }
    }

    override fun getLoopInterval(): Int {
        return 5000
    }

    override fun onItemViewAttachedToWindow(itemHolder: DslViewHolder, itemPosition: Int) {
        super.onItemViewAttachedToWindow(itemHolder, itemPosition)
        L.i(itemPosition)
    }

    override fun onItemViewDetachedToWindow(itemHolder: DslViewHolder, itemPosition: Int) {
        super.onItemViewDetachedToWindow(itemHolder, itemPosition)
        L.w(itemPosition)
    }

    override fun onItemViewRecycled(itemHolder: DslViewHolder, itemPosition: Int) {
        super.onItemViewRecycled(itemHolder, itemPosition)
        L.e(itemPosition)
    }
}