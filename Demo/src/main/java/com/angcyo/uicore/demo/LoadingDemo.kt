package com.angcyo.uicore.demo

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.angcyo.drawable.loading.TGSolidLoadingDrawable
import com.angcyo.drawable.loading.TGStrokeLoadingDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.github.dialog.colorPickerDialog
import com.angcyo.library.ex.anim
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.setBgDrawable
import com.angcyo.library.ex.toColorInt
import com.angcyo.uicore.MainFragment
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.progress.DslBlockSeekBar
import com.angcyo.widget.progress.DslProgressBar

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
                val tgStrokeLoadingDrawable2 = TGStrokeLoadingDrawable().apply {
                    loadingOffset = 6 * dp
                    loadingWidth = 6 * dp
                    indeterminateSweepAngle = 1f
                    loadingBgColor = "#999999".toColorInt()
                    loadingColor = loadingBgColor
                }

                //set
                itemHolder.view(R.id.view1)?.setBgDrawable(tgSolidLoadingDrawable)
                itemHolder.view(R.id.view2)?.setBgDrawable(tgStrokeLoadingDrawable)
                itemHolder.view(R.id.view3)?.setBgDrawable(tgStrokeLoadingDrawable2)

                //control
                itemHolder.click(R.id.indeterminate_button) {
                    tgSolidLoadingDrawable.isIndeterminate = true
                    tgStrokeLoadingDrawable2.isIndeterminate = true
                    tgStrokeLoadingDrawable.isIndeterminate = true
                }
                itemHolder.click(R.id.progress_button) {
                    tgSolidLoadingDrawable.isIndeterminate = false
                    tgStrokeLoadingDrawable.isIndeterminate = false
                    tgStrokeLoadingDrawable2.isIndeterminate = false

                    anim(0, 100) {
                        onAnimatorConfig = {
                            it.duration = 3_000
                        }
                        onAnimatorUpdateValue = { value, fraction ->
                            val progress = value as Int
                            tgSolidLoadingDrawable.progress = progress
                            tgStrokeLoadingDrawable.progress = progress
                            tgStrokeLoadingDrawable2.progress = progress
                        }
                    }
                }

                //progress
                itemHolder.v<DslProgressBar>(R.id.progress_bar)?.apply {
                    setProgress(50)
                }

                itemHolder.v<DslBlockSeekBar>(R.id.seek_bar)?.apply {
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            itemHolder.v<DslProgressBar>(R.id.progress_bar)
                                ?.setProgress(value, value)
                        }
                    }
                }

                //color
                itemHolder.click(R.id.color_picker_button) {
                    val seekBar = itemHolder.v<DslBlockSeekBar>(R.id.seek_bar)
                    fContext().colorPickerDialog {
                        initialColor =
                            (seekBar?.progressBgDrawable as? GradientDrawable)?.color?.defaultColor
                                ?: initialColor
                        colorPickerAction = { dialog, color ->
                            seekBar?.setBgGradientColors("$color")
                            false
                        }

                        if (MainFragment.CLICK_COUNT++ % 2 == 0) {
                            paletteDrawable =
                                ContextCompat.getDrawable(fContext(), R.drawable.palettebar)
                        }
                    }
                }
            }
        }
    }

}