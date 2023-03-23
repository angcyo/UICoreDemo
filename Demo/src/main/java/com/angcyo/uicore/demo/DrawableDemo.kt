package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.DrawSkewView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/03/23
 */
class DrawableDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_drawable) { itemHolder, itemPosition, adapterItem, payloads ->

            }
        }
    }
}