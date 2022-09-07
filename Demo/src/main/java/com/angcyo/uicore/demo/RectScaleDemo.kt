package com.angcyo.uicore.demo

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.L
import com.angcyo.library.component.pool.acquireTempMatrix
import com.angcyo.library.component.pool.release
import com.angcyo.library.ex.computePathBounds
import com.angcyo.library.ex.mH
import com.angcyo.library.ex.mW
import com.angcyo.library.ex.toDC
import com.angcyo.library.gesture.RectScaleGestureHandler
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.RectScaleView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/16
 */
class RectScaleDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.demo_rect_scale_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val rectScaleView = itemHolder.v<RectScaleView>(R.id.rect_scale_view)

                //radio
                itemHolder.tv(R.id.keep_button)?.text = "Keep ${rectScaleView?.keepRadio.toDC()}"
                itemHolder.click(R.id.keep_button) {
                    rectScaleView?.apply {
                        keepRadio = !keepRadio
                    }
                    itemHolder.tv(R.id.keep_button)?.text =
                        "Keep ${rectScaleView?.keepRadio.toDC()}"
                }

                //rotate
                itemHolder.tv(R.id.rotate_button)?.text = "${rectScaleView?.rotate} °"
                itemHolder.click(R.id.rotate_button) {
                    rectScaleView?.apply {
                        rotate = (rotate + 10) % 360
                    }
                    itemHolder.tv(R.id.rotate_button)?.text = "${rectScaleView?.rotate} °"
                    rectScaleView?.invalidate()
                }

                //position
                itemHolder.click(R.id.left_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_LEFT
                }
                itemHolder.click(R.id.top_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_TOP
                }
                itemHolder.click(R.id.right_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_RIGHT
                }
                itemHolder.click(R.id.bottom_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_BOTTOM
                }

                //
                itemHolder.click(R.id.lt_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_LT
                }
                itemHolder.click(R.id.rt_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_RT
                }
                itemHolder.click(R.id.rb_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_RB
                }
                itemHolder.click(R.id.lb_button) {
                    rectScaleView?.rectPosition = RectScaleGestureHandler.RECT_LB
                }
                itemHolder.click(R.id.p1_button) {
                    rectScaleView?.anchorPoint =
                        PointF(rectScaleView.mW() * 1 / 5f, rectScaleView.mH() * 1 / 5f)
                }

                itemHolder.click(R.id.test_scale_button) {
                    rectScaleView?.testScale()
                }

                //test
                itemHolder.click(R.id.test_button) {
                    //rectScaleView?.test()
                    rectScaleView?.test2()
                    test(RectF(0f, 0f, 100f, 100f))
                    test(RectF(10f, 10f, 100f, 100f))
                    test(RectF(10f, 10f, -90f, -90f))
                    //rectScaleView?.testLine()
                }

                //test2
                itemHolder.click(R.id.test_button2) {
                    rectScaleView?.test3()
                }

                //test3
                itemHolder.click(R.id.test_button3) {
                    rectScaleView?.test4()
                }

                itemHolder.click(R.id.test_scale_frame_button) {
                    rectScaleView?.testScaleFrame()
                }
                itemHolder.click(R.id.test_height_button) {
                    rectScaleView?.testHeight()
                }
            }
        }
    }

    fun test(rect: RectF) {
        val matrix = Matrix()
        val result = RectF()

        matrix.setScale(-1f, -1f)
        matrix.mapRect(result, rect)
        L.i(result, result.width(), result.height())

        matrix.setScale(-1.5f, -1.2f)
        matrix.mapRect(result, rect)
        L.i(result, result.width(), result.height())

        matrix.setScale(-1.5f, -1.2f, rect.centerX(), rect.centerY())
        matrix.mapRect(result, rect)
        L.i(result, result.width(), result.height())

        matrix.setScale(-1.5f, -1.2f, rect.left, rect.top)
        matrix.mapRect(result, rect)
        L.i(result, result.width(), result.height())
    }



}