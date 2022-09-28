package com.angcyo.uicore.demo

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import com.angcyo.drawable.CheckerboardDrawable
import com.angcyo.drawable.base.dslGradientDrawable
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex._color
import com.angcyo.library.ex.copy
import com.angcyo.library.ex.toHexColorString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.clickIt
import top.defaults.colorpicker.ColorPickerPopup
import top.defaults.colorpicker.ColorPickerPopup.ColorPickerObserver
import top.defaults.colorpicker.ColorPickerView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/02
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class GithubDemo : AppDslFragment() {

    var defaultColor = _color(R.color.colorPrimary)

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.item_github1
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.view(R.id.image_view)?.apply {
                        background = CheckerboardDrawable.create()

                        clickIt {
                            ColorPickerPopup.Builder(context)
                                .initialColor(defaultColor)
                                .enableAlpha(true)
                                .okTitle("Choose")
                                .cancelTitle("Cancel")
                                .showIndicator(true)
                                .showValue(true)
                                .onlyUpdateOnTouchEventUp(true)
                                .build()
                                .show(object : ColorPickerObserver() {
                                    override fun onColorPicked(color: Int) {
                                        onColorPicker(itemHolder, color, true)
                                    }
                                })
                        }
                    }

                    itemHolder.v<ColorPickerView>(R.id.color_picker_view)?.apply {
                        setInitialColor(defaultColor)
                        subscribe { color, fromUser, shouldPropagate ->
                            onColorPicker(itemHolder, color, fromUser)
                        }
                    }
                }
            }
        }
    }

    fun onColorPicker(itemHolder: DslViewHolder, color: Int, fromUser: Boolean = false) {
        defaultColor = color

        itemHolder.tv(R.id.text_view)?.apply {
            text = color.toHexColorString()
            clickIt {
                text.copy()
            }
        }

        itemHolder.img(R.id.image_view)?.apply {
            if (fromUser) {
                setImageDrawable(dslGradientDrawable {
                    //cornerRadius(mH() / 2f)
                    gradientShape = GradientDrawable.OVAL
                    gradientSolidColor = color
                })
            }
        }
    }
}