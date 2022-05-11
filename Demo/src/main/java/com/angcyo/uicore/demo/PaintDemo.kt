package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.os.Bundle
import com.angcyo.drawable.StripeDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.setBgDrawable
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

                val view = itemHolder.view(R.id.stripe_view)
                val view2 = itemHolder.view(R.id.stripe_view2)
                val view3 = itemHolder.view(R.id.stripe_view3)

                val stripeDrawable = StripeDrawable().apply {
                    stripeHeight = 20 * dp
                    intervalHeight = stripeHeight / 2
                }
                val stripeDrawable2 = StripeDrawable().apply {
                    stripeHeight = 10 * dp
                    intervalHeight = stripeHeight / 2
                }
                val stripeDrawable3 = StripeDrawable().apply {
                    stripeHeight = 10 * dp
                    intervalHeight = stripeHeight / 2
                }
                view.setBgDrawable(stripeDrawable)
                view2.setBgDrawable(stripeDrawable2)
                view3.setBgDrawable(stripeDrawable3)

                itemHolder.click(R.id.rotate_button) {
                    stripeDrawable.rotate++
                    stripeDrawable.offsetStep = 1f
                    stripeDrawable.update()

                    stripeDrawable2.rotate++
                    stripeDrawable2.offsetStep = -1f
                    stripeDrawable2.update()

                    stripeDrawable3.rotate = 30f
                    stripeDrawable3.offsetStep = -1f
                    stripeDrawable3.borderColor = Color.RED
                    stripeDrawable3.borderWidth = 3 * dp
                    stripeDrawable3.borderPath = Path().apply {
                        addRoundRect(
                            RectF(stripeDrawable3.bounds),
                            10 * dp,
                            10 * dp,
                            Path.Direction.CW
                        )
                    }
                    stripeDrawable3.update()
                }
            }
        }
    }

}