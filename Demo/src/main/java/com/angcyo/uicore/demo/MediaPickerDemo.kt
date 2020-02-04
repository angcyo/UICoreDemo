package com.angcyo.uicore.demo

import android.content.Intent
import android.os.Bundle
import com.angcyo.core.R
import com.angcyo.core.utils.resultString
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.findItemByTag
import com.angcyo.library.ex._color
import com.angcyo.picker.DslPicker
import com.angcyo.picker.dslPicker
import com.angcyo.picker.dslitem.DslPickerImageItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppMediaPickerItem
import com.angcyo.widget.recycler.resetLayoutManager
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/29
 */

class MediaPickerDemo : AppDslFragment() {

    override fun onInitDslLayout() {
        super.onInitDslLayout()
        _vh.rv(R.id.lib_recycler_view)?.resetLayoutManager("GV4")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            changeHeaderItems {
                it.add(AppMediaPickerItem().apply {
                    itemSpanCount = -1
                    onStartPicker = { loaderConfig ->
                        dataItems.forEach { item ->
                            if (item is DslPickerImageItem) {
                                item.loaderMedia?.apply {
                                    loaderConfig.selectorMediaList.add(this)
                                }
                            }
                        }
                        dslPicker(loaderConfig)
                    }
                })
                it.add(DslAdapterItem().apply {
                    itemSpanCount = -1
                    itemTag = "result"
                    itemLayoutId = R.layout.lib_text_layout
                    onItemBindOverride = { itemHolder, _, _, _ ->
                        itemHolder.tv(R.id.lib_text_view)?.text = itemData as? CharSequence
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DslPicker.onActivityResult(requestCode, resultCode, data)?.apply {
            renderDslAdapter {
                loadSingleData(this@apply, 1, 20) { oldItem, _ ->
                    oldItem ?: DslPickerImageItem().apply {
                        checkModel = false
                    }
                }
            }
        }

        _adapter.findItemByTag("result")?.apply {
            itemData = span {
                append("requestCode:")
                append("$requestCode") {
                    foregroundColor = _color(R.color.colorPrimaryDark)
                }
                appendln()

                append("resultCode:")
                append("$resultCode ${resultCode.resultString()}") {
                    foregroundColor = _color(R.color.colorPrimaryDark)
                }
                appendln()

                append("data:")
                append("$data") {
                    foregroundColor = _color(R.color.colorPrimaryDark)
                }
                appendln()

                DslPicker.onActivityResult(requestCode, resultCode, data)?.run {
                    append(this.toString())
                }
            }

            updateAdapterItem()
        }
    }
}