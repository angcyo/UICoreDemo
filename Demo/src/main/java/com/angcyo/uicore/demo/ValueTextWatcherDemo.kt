package com.angcyo.uicore.demo

import android.os.Bundle
import android.text.format.Formatter
import com.angcyo.dsladapter.dslItem
import com.angcyo.library.L
import com.angcyo.library.ex.formatFileSize
import com.angcyo.widget.base.clearListeners
import com.angcyo.widget.base.onTextChange
import com.angcyo.widget.edit.ValueTextWatcher
import com.angcyo.widget.span.span

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

                onItemBindOverride = { itemHolder, _, _,_ ->
                    val editText = itemHolder.et(R.id.edit_text)
                    val maxEditText = itemHolder.et(R.id.max_edit_text)
                    val minEditText = itemHolder.et(R.id.min_edit_text)

                    //清理复用的listener
                    editText?.clearListeners()
                    maxEditText?.clearListeners()
                    minEditText?.clearListeners()

                    //安装最新的ValueTextWatcher
                    fun updateFilter() {
                        editText?.clearListeners()
                        ValueTextWatcher.install(editText) {
                            maxFilterValue = itemMaxValue
                            minFilterValue = itemMinValue
                        }
                    }

                    //设置最大输入值
                    maxEditText?.onTextChange {
                        L.i("max:$it")
                        itemHolder.tv(R.id.lib_text_view)?.text = "max:$it"
                        itemMaxValue = it.toString().toFloatOrNull() ?: itemMaxValue
                        updateFilter()
                    }

                    //社会最小输入值
                    minEditText?.onTextChange {
                        L.i("min:$it")
                        itemHolder.tv(R.id.lib_text_view)?.text = "min:$it"
                        itemMinValue = it.toString().toFloatOrNull() ?: itemMinValue
                        updateFilter()
                    }

                    //文件大小格式化输出
                    editText?.onTextChange {
                        itemHolder.tv(R.id.lib_text_view)?.text = span {
                            it.toString().toFloatOrNull()?.toLong()?.let {
                                append(Formatter.formatFileSize(fContext(), it))
                                appendln()
                                append(Formatter.formatShortFileSize(fContext(), it))
                                appendln()
                                append(formatFileSize(fContext(), it))
                            }
                        }
                    }
                }
            }
        }
    }
}