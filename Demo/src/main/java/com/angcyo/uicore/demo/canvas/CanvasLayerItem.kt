package com.angcyo.uicore.demo.canvas

import android.graphics.drawable.Drawable
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.core.renderer.SelectGroupRenderer
import com.angcyo.canvas.items.renderer.BaseItemRenderer
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._drawable
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/09
 */
class CanvasLayerItem : DslAdapterItem() {

    var itemCanvasDelegate: CanvasDelegate? = null

    var itemRenderer: BaseItemRenderer<*>? = null

    val itemLayerHide: Boolean get() = itemRenderer?.isVisible() == false

    val itemLayerName: CharSequence? get() = itemRenderer?.getName()

    val itemItemDrawable: Drawable? get() = itemRenderer?.getRendererItem()?.itemDrawable

    val itemItemName: CharSequence? get() = itemRenderer?.getRendererItem()?.itemName

    init {
        itemLayoutId = R.layout.item_canvas_layer_layout

        itemClick = {
            itemRenderer?.let {
                val selectedRenderer = itemCanvasDelegate?.getSelectedRenderer()
                if (selectedRenderer is SelectGroupRenderer) {
                    //no
                } else {
                    itemCanvasDelegate?.selectedItem(it)
                }
                if (it.isVisible()) {
                    itemCanvasDelegate?.showRectBounds(it.getRotateBounds())
                }
            }
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        val selectedRenderer = itemCanvasDelegate?.getSelectedRenderer()
        if (selectedRenderer is SelectGroupRenderer) {
            itemRenderer?.let {
                itemIsSelected = selectedRenderer.selectItemList.contains(it)
            }
            itemHolder.visible(R.id.background_view, false)
        } else {
            itemIsSelected = itemRenderer == selectedRenderer
            itemHolder.visible(R.id.background_view, itemIsSelected)
        }

        itemHolder.tv(R.id.layer_name_view)?.text = itemLayerName
        if (itemLayerHide) {
            itemHolder.img(R.id.layer_visible_view)
                ?.setImageDrawable(_drawable(R.drawable.canvas_layer_hide))
        } else {
            itemHolder.img(R.id.layer_visible_view)
                ?.setImageDrawable(_drawable(R.drawable.canvas_layer_visible))
        }

        itemHolder.tv(R.id.item_name_view)?.text = itemItemName
        itemHolder.img(R.id.item_drawable_view)
            ?.setImageDrawable(itemItemDrawable ?: itemRenderer?.preview())

        itemHolder.selected(R.id.lib_check_view, itemIsSelected)

        //事件
        itemHolder.click(R.id.layer_visible_view) {
            itemRenderer?.setVisible(itemLayerHide)
            itemCanvasDelegate?.refresh()
            updateAdapterItem()
        }

        itemHolder.click(R.id.lib_check_view) {
            itemIsSelected = !itemIsSelected
            itemRenderer?.let {
                if (itemIsSelected) {
                    itemCanvasDelegate?.selectGroupRenderer?.addSelectedRenderer(it)
                } else {
                    itemCanvasDelegate?.selectGroupRenderer?.removeSelectedRenderer(it)
                }
            }
            updateAdapterItem()
        }
    }

}