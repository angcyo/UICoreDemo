package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.angcyo.dialog.TargetWindow
import com.angcyo.dialog.popup.PopupTipConfig
import com.angcyo.dialog.popup.popupTipWindow
import com.angcyo.drawable.BubbleDrawable
import com.angcyo.drawable.progress.CircleProgressDrawable
import com.angcyo.drawable.progress.LinearProgressDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.library._screenWidth
import com.angcyo.library.ex.interceptParentTouchEvent
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.clickIt
import com.angcyo.widget.progress.DslBlockSeekBar
import com.angcyo.widget.progress.DslProgressBar

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/03
 */
class LoadingDemo2 : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.item_loading_layout2) { itemHolder, itemPosition, adapterItem, payloads ->

                //
                val circleProgressDrawable = CircleProgressDrawable()
                itemHolder.view(R.id.view1)?.apply {
                    //circleProgressDrawable.startOffsetAngle = 11f
                    //circleProgressDrawable.progressWidth = 40f
                    circleProgressDrawable.updateProgressWidth(40f)
                    background = circleProgressDrawable
                    clickIt {
                        if (circleProgressDrawable.maxProgressValue == circleProgressDrawable.currentProgressValue) {
                            circleProgressDrawable.updateProgressValue(circleProgressDrawable.minProgressValue)
                        } else {
                            circleProgressDrawable.updateProgressValue(circleProgressDrawable.maxProgressValue)
                        }
                    }
                }

                //
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

                //
                itemHolder.view(R.id.view3)?.background = BubbleDrawable()

                //
                itemHolder.v<DslBlockSeekBar>(R.id.seek_bar)?.config {
                    onSeekChanged = { value, fraction, fromUser ->
                        if (fromUser) {
                            circleProgressDrawable.currentProgressValue = value
                            linearProgressDrawable.currentProgressValue = value
                        }
                    }
                }
                itemHolder.touch(R.id.seek_bar) { view, event ->
                    showPopupTip(view, event)
                    true
                }

                //
                itemHolder.touch(R.id.popup_tip) { view, event ->
                    showPopupTip(view, event)
                    true
                }
            }
        }
    }

    var window: TargetWindow? = null
    var popupTipConfig: PopupTipConfig? = null

    fun showPopupTip(view: View, event: MotionEvent) {
        view.interceptParentTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                window = fContext().popupTipWindow(view, R.layout.layout_bubble_tip) {
                    touchX = event.x
                    popupTipConfig = this
                    onInitLayout = { window, viewHolder ->
                        viewHolder.view(R.id.view)?.background = BubbleDrawable()
                        viewHolder.tv(R.id.text_view)?.text = if (view is DslProgressBar) {
                            "${view.progressValue}"
                        } else {
                            "${(touchX * 1f / _screenWidth * 100).toInt()}"
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                popupTipConfig?.apply {
                    touchX = event.x
                    updatePopup()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //window?.dismiss()
                popupTipConfig?.hide()
            }
        }
    }
}