package com.angcyo.uicore.dslitem

import android.net.Uri
import com.angcyo.base.dslAHelper
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.tbs.open
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.clearListeners
import com.angcyo.widget.base.onTextChange
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.base.string
import org.jsoup.nodes.Document

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/14
 */
class AppJsoupInputItem : DslAdapterItem() {

    var jsoupData = JsoupData("https://www.wo03.com/", selectCss = "title")

    init {
        itemLayoutId = R.layout.item_jsoup_input

        isItemInUpdateList = { checkItem, itemIndex ->
            checkItem is AppJsoupHtmlItem
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.ev(R.id.url_edit)?.apply {
            clearListeners()
            onTextChange {
                jsoupData.jsoupUrl = it

                itemChanging = true
            }
            setInputText(jsoupData.jsoupUrl)
        }

        itemHolder.ev(R.id.select_edit)?.setInputText(jsoupData.selectCss)

        //获取url网页的内容
        itemHolder.click(R.id.get_button) {
            jsoupData.getUrlHtml = true
            itemChanging = true
        }

        //执行select
        itemHolder.click(R.id.select_button) {
            jsoupData.selectCss = itemHolder.tv(R.id.select_edit).string()
            jsoupData.getUrlSelect = true
            itemChanging = true
        }

        //打开页面
        itemHolder.click(R.id.open_button) {
            itemHolder.content.dslAHelper {
                open {
                    uri = Uri.parse(jsoupData.jsoupUrl.toString())
                }
            }
        }
    }
}

data class JsoupData(
    var jsoupUrl: CharSequence? = null,
    var getUrlHtml: Boolean = false,
    var selectCss: String? = null,
    var getUrlSelect: Boolean = false,

    var document: Document? = null
)