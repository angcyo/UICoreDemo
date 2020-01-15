package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.library.L
import com.angcyo.widget.base.clearListeners
import com.angcyo.widget.base.onTextChange
import com.angcyo.widget.edit.ValueTextWatcher

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/14
 */

class ValueTextWatcherDemo : DslSoftInputDemo() {

    init {
        enableSoftInput = false
        contentLayoutId = R.layout.demo_soft_input_multi_layout
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            dslItem(R.layout.item_number_input_filter_layout) {

                var itemMaxValue = ValueTextWatcher.MAX_VALUE
                var itemMinValue = ValueTextWatcher.MIN_VALUE

                onItemBindOverride = { itemHolder, _, _ ->
                    val editText = itemHolder.et(R.id.edit_text)
                    val maxEditText = itemHolder.et(R.id.max_edit_text)
                    val minEditText = itemHolder.et(R.id.min_edit_text)

                    editText?.clearListeners()
                    maxEditText?.clearListeners()
                    minEditText?.clearListeners()

                    fun updateFilter() {
                        editText?.clearListeners()
                        ValueTextWatcher.install(editText) {
                            maxFilterValue = itemMaxValue
                            minFilterValue = itemMinValue
                        }
                    }

                    maxEditText?.onTextChange {
                        L.i("max:$it")
                        itemHolder.tv(R.id.text_view)?.text = "max:$it"
                        itemMaxValue = it.toString().toFloatOrNull() ?: itemMaxValue
                        updateFilter()
                    }

                    minEditText?.onTextChange {
                        L.i("min:$it")
                        itemHolder.tv(R.id.text_view)?.text = "min:$it"
                        itemMinValue = it.toString().toFloatOrNull() ?: itemMinValue
                        updateFilter()
                    }
                }
            }
        }
    }
}