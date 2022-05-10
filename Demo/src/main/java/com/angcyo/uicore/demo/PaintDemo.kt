package com.angcyo.uicore.demo

import android.graphics.Shader
import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.PaintView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/10
 */
class PaintDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_paint) { itemHolder, itemPosition, adapterItem, payloads ->
                val paintView = itemHolder.v<PaintView>(R.id.paint_view)?.apply {
                    //tile mode
                    itemHolder.click(R.id.tile_clamp) {
                        titleMode = Shader.TileMode.CLAMP
                    }
                    itemHolder.click(R.id.tile_repeat) {
                        titleMode = Shader.TileMode.REPEAT
                    }
                    itemHolder.click(R.id.tile_mirror) {
                        titleMode = Shader.TileMode.MIRROR
                    }
                    itemHolder.click(R.id.tile_clamp) {
                        titleMode = Shader.TileMode.CLAMP
                    }

                    //shader
                    itemHolder.click(R.id.bitmap_shader) {
                        paint.shader = createBitmapShader()
                        invalidate()
                    }
                    itemHolder.click(R.id.linear_shader) {
                        paint.shader = createLinearShader()
                        invalidate()
                    }
                    itemHolder.click(R.id.sweep_shader) {
                        paint.shader = createSweepShader()
                        invalidate()
                    }
                    itemHolder.click(R.id.radial_shader) {
                        paint.shader = createRadialShader()
                        invalidate()
                    }
                }
            }
        }
    }

}