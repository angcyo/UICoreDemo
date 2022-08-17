package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
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

                //test
                itemHolder.click(R.id.test_button) {
                    rectScaleView?.test()
                }
            }
        }
    }

}