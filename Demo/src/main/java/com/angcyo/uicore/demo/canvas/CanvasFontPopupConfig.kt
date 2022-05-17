package com.angcyo.uicore.demo.canvas

import android.content.Context
import android.graphics.Typeface
import android.view.View
import com.angcyo.canvas.items.PictureTextItem
import com.angcyo.canvas.items.renderer.IItemRenderer
import com.angcyo.canvas.items.renderer.PictureItemRenderer
import com.angcyo.canvas.items.renderer.PictureTextItemRenderer
import com.angcyo.canvas.items.renderer.TextItemRenderer
import com.angcyo.dialog.TargetWindow
import com.angcyo.dialog.popup.ShadowAnchorPopupConfig
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.drawBottom
import com.angcyo.library.ex._dimen
import com.angcyo.library.ex._string
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.recycler.renderDslAdapter

/**
 * 画图字体选择
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/16
 */
class CanvasFontPopupConfig : ShadowAnchorPopupConfig() {

    /**操作的渲染项*/
    var itemRenderer: IItemRenderer<*>? = null

    init {
        contentLayoutId = R.layout.canvas_font_layout
        triangleMinMargin = 24 * dpi
        yoff = -10 * dpi
    }

    override fun initContentLayout(window: TargetWindow, viewHolder: DslViewHolder) {
        super.initContentLayout(window, viewHolder)
        viewHolder.rv(R.id.lib_recycler_view)?.renderDslAdapter {
            typefaceItem("normal", Typeface.DEFAULT)
            typefaceItem("sans", Typeface.SANS_SERIF)
            typefaceItem("serif", Typeface.SERIF)
            typefaceItem("Default-Normal", Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            typefaceItem("Default-Bold", Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            typefaceItem("Default-Italic", Typeface.create(Typeface.DEFAULT, Typeface.ITALIC))
            typefaceItem(
                "Default-Bold-Italic",
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC),
                false
            )
        }
    }

    fun DslAdapter.typefaceItem(name: String, type: Typeface, line: Boolean = true) {
        TypefaceItem()() {
            displayName = name
            previewText = _string(R.string.canvas_font_text)
            typeface = type
            if (line) {
                drawBottom(_dimen(R.dimen.lib_line_px), 0, 0)
            }
            itemClick = {
                updatePaintTypeface(typeface)
            }
        }
    }

    //更新字体
    fun updatePaintTypeface(typeface: Typeface?) {
        val renderer = itemRenderer
        if (renderer is TextItemRenderer) {
            renderer.updatePaintTypeface(typeface)
        } else if (renderer is PictureTextItemRenderer) {
            renderer.updatePaintTypeface(typeface)
        } else if (renderer is PictureItemRenderer) {
            val renderItem = renderer._rendererItem
            if (renderItem is PictureTextItem) {
                renderer.updateTextTypeface(typeface)
            }
        }
    }
}

/**Dsl*/
fun Context.canvasFontWindow(anchor: View?, config: CanvasFontPopupConfig.() -> Unit): Any {
    val popupConfig = CanvasFontPopupConfig()
    popupConfig.anchor = anchor
    popupConfig.config()
    return popupConfig.show(this)
}