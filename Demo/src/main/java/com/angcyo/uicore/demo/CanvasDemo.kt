package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.canvas.CanvasView
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex._colorDrawable
import com.angcyo.library.ex.toColorInt
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.setBgDrawable

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/01
 */
class CanvasDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.item_canvas_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.v<CanvasView>(R.id.canvas_view)
                    //?.setBgDrawable(_colorDrawable("#20000000".toColorInt()))
                //?.setBgDrawable(CheckerboardDrawable.create())
            }
        }
    }

}