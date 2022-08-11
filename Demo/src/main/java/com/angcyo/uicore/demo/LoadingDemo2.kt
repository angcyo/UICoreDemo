package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.drawable.progress.CircleProgressDrawable
import com.angcyo.drawable.progress.LinearProgressDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.clickIt
import com.angcyo.widget.progress.DslBlockSeekBar

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/03
 */
class LoadingDemo2 : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.item_loading_layout2) { itemHolder, itemPosition, adapterItem, payloads ->

                val circleProgressDrawable = CircleProgressDrawable()
                itemHolder.view(R.id.view1)?.apply {
                    //circleProgressDrawable.startOffsetAngle = 11f
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

                val linearProgressDrawable = LinearProgressDrawable()
                itemHolder.view(R.id.view2)?.apply {
                    background = linearProgressDrawable
                    clickIt {
                        if (linearProgressDrawable.maxProgressValue == linearProgressDrawable.currentProgressValue) {
                            linearProgressDrawable.updateProgressValue(linearProgressDrawable.minProgressValue)
                        } else {
                            linearProgressDrawable.updateProgressValue(linearProgressDrawable.maxProgressValue)
                        }
                    }
                }

                itemHolder.v<DslBlockSeekBar>(R.id.seek_bar)?.config {
                    onSeekChanged = { value, fraction, fromUser ->
                        if (fromUser) {
                            circleProgressDrawable.currentProgressValue = value
                            linearProgressDrawable.currentProgressValue = value
                        }
                    }
                }

            }
        }
    }
}