package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.drawable.loading.TGSolidLoadingDrawable
import com.angcyo.drawable.loading.TGStrokeLoadingDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.anim
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.setBgDrawable

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/04/16
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class LoadingDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.item_loading_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val tgSolidLoadingDrawable = TGSolidLoadingDrawable()
                val tgStrokeLoadingDrawable = TGStrokeLoadingDrawable()

                //set
                itemHolder.view(R.id.view1)?.setBgDrawable(tgSolidLoadingDrawable)
                itemHolder.view(R.id.view2)?.setBgDrawable(tgStrokeLoadingDrawable)

                //control
                itemHolder.click(R.id.indeterminate_button) {
                    tgSolidLoadingDrawable.isIndeterminate = true
                    tgStrokeLoadingDrawable.isIndeterminate = true
                }
                itemHolder.click(R.id.progress_button) {
                    tgSolidLoadingDrawable.isIndeterminate = false
                    tgStrokeLoadingDrawable.isIndeterminate = false

                    anim(0, 100) {
                        onAnimatorConfig = {
                            it.duration = 3_000
                        }
                        onAnimatorUpdateValue = { value, fraction ->
                            tgSolidLoadingDrawable.progress = value as Int
                            tgStrokeLoadingDrawable.progress = value as Int
                        }
                    }
                }
            }
        }
    }

}