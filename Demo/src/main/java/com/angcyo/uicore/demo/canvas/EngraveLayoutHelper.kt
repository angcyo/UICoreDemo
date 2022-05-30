package com.angcyo.uicore.demo.canvas

import androidx.fragment.app.Fragment
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.core.CanvasEntryPoint
import com.angcyo.core.vmApp

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/30
 */
class EngraveLayoutHelper(val fragment: Fragment) {

    /**绑定布局*/
    @CanvasEntryPoint
    fun bindLayout(canvasView: CanvasView) {
        vmApp<LaserPeckerModel>().productInfoData.observe(fragment) { productInfo ->
            if (productInfo == null) {
                canvasView.canvasDelegate.limitRenderer.clear()
            } else {
                if (productInfo.isOriginCenter) {
                    canvasView.canvasDelegate.moveOriginToCenter()
                } else {
                    canvasView.canvasDelegate.moveOriginToLT()
                }
                canvasView.canvasDelegate.showAndLimitBounds(productInfo.limitPath)
            }
        }
    }

}