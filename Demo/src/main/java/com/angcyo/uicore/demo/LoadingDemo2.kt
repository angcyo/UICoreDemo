package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.drawable.progress.CircleProgressDrawable
import com.angcyo.drawable.progress.LinearProgressDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.clickIt

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/03
 */
class LoadingDemo2 : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.item_loading_layout2) { itemHolder, itemPosition, adapterItem, payloads ->

                itemHolder.view(R.id.view1)?.apply {
                    val circleProgressDrawable = CircleProgressDrawable()
                    circleProgressDrawable.startOffsetAngle = 11f
                    circleProgressDrawable.progressWidth = 40f
                    background = circleProgressDrawable
                    clickIt {
                        if (circleProgressDrawable.maxProgressValue == circleProgressDrawable.currentProgressValue) {
                            circleProgressDrawable.updateProgressValue(circleProgressDrawable.minProgressValue)
                        } else {
                            circleProgressDrawable.updateProgressValue(circleProgressDrawable.maxProgressValue)
                        }
                    }
                }

                itemHolder.view(R.id.view2)?.apply {
                    val linearProgressDrawable = LinearProgressDrawable()
                    background = linearProgressDrawable
                    clickIt {
                        if (linearProgressDrawable.maxProgressValue == linearProgressDrawable.currentProgressValue) {
                            linearProgressDrawable.updateProgressValue(linearProgressDrawable.minProgressValue)
                        } else {
                            linearProgressDrawable.updateProgressValue(linearProgressDrawable.maxProgressValue)
                        }
                    }
                }

            }
        }
    }
}