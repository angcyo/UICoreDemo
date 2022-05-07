package com.angcyo.uicore.demo.canvas

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._color
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.color
import com.angcyo.library.ex.toColorInt
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * 只有图标的item
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/29
 */
open class CanvasIconItem : DslAdapterItem() {

    /**图标资源*/
    @DrawableRes
    var itemIco: Int = 0

    /**图标是否要着色*/
    var itemTintColor: Boolean = true

    /**图标的颜色*/
    @ColorInt
    var itemIcoColor: Int = "#363d4b".toColorInt()

    /**被禁用时的图标颜色*/
    @ColorInt
    var itemIcoDisableColor: Int = "#aab1bd".toColorInt()

    /**高亮时的图标颜色*/
    @ColorInt
    var itemIcoSelectedColor: Int = _color(R.color.colorAccent)

    val itemColor: Int
        get() = if (itemEnable) {
            if (itemIsSelected) {
                itemIcoSelectedColor
            } else {
                itemIcoColor
            }
        } else {
            itemIcoDisableColor
        }

    init {
        itemLayoutId = R.layout.item_canvas_icon_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        val drawable = _drawable(itemIco)?.run {
            if (itemTintColor) {
                color(itemColor)
            } else {
                this
            }
        }
        itemHolder.img(R.id.image_view)?.setImageDrawable(drawable)
    }

}