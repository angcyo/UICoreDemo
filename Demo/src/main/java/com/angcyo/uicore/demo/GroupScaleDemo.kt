package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.demo.draw.GroupScaleView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/01
 */
class GroupScaleDemo : MatrixSkewDemo() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter(true) {
            bindItem(R.layout.demo_group_scale) { itemHolder, itemPosition, adapterItem, payloads ->
                val demoView = itemHolder.v<GroupScaleView>(R.id.group_scale_view)

                //---rotate
                bindRotate(itemHolder, demoView)

                //---scale
                bindScale(itemHolder, demoView)

                //---skew
                bindSkew(itemHolder, demoView)
            }
        }
    }
}