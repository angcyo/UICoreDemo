package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.View
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._color
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/17
 */

class StyleDemo : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderDslAdapter {
            DslAdapterItem()() {
                itemLayoutId = R.layout.lib_text_layout

                onItemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.tv(R.id.lib_text_view)?.text = span {

                        val typedArray = fContext().obtainStyledAttributes(
                            R.style.StylesDemo,
                            R.styleable.StylesDemoStyleable
                        )

                        for (i in 0 until typedArray.length()) {
                            appendln()

                            val index = typedArray.getIndex(i)
                            val type = typedArray.getType(index)
                            val value = typedArray.peekValue(index)

                            append("$i->")
                            append(" type:") {
                                foregroundColor = _color(R.color.colorPrimaryDark)
                            }
                            append("$type")
                            append(" value:") {
                                foregroundColor = _color(R.color.colorPrimaryDark)
                            }
                            append("$value")

                            appendln()

                            append("${value.string}") {
                                foregroundColor = _color(R.color.colorPrimaryDark)
                            }
                            append(" ${typedArray.getString(index)}")

                            appendln()
                        }

                        typedArray.recycle()
                    }
                }
            }
        }
    }
}