package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.fullWidthItem
import com.angcyo.dsladapter.removeItem
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslInputItem
import com.angcyo.item.style.itemButtonText
import com.angcyo.item.style.itemEditText
import com.angcyo.library.ex.toStr
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.dslitem.BarcodeFormatItem
import com.angcyo.widget.recycler.resetLayoutManager
import com.google.zxing.BarcodeFormat

/**
 * 枚举条码/二维码格式, 并生成对应图片
 * [com.google.zxing.BarcodeFormat]
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/09/09
 */
class BarcodeFormatDemo : AppDslFragment() {

    var barcodeContent: String = "angcyo123456"

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        try {
            val name = BarcodeFormat.EAN_13.name
            val str = "${BarcodeFormat.EAN_13}"

            BarcodeFormat.valueOf(name)
            //BarcodeFormat.valueOf("")//No enum constant com.google.zxing.BarcodeFormat
            //name == str
        } catch (e: Exception) {
            e.printStackTrace()
        }

        renderDslAdapter {
            DslInputItem()() {
                fullWidthItem()
                //multiLineEditMode()
                itemEditText = barcodeContent
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.hawkInstallAndRestore("_barcode_format")
                }
                observeItemChange {
                    barcodeContent = itemEditText.toStr()
                }
            }
            DslButtonItem()() {
                fullWidthItem()
                itemButtonText = "生成条码"
                itemClick = {
                    renderBarcodeFormat()
                }
            }
        }
    }

    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        recyclerView.resetLayoutManager("GV4")
    }

    private fun renderBarcodeFormat() {
        _adapter.render {
            removeItem { it is BarcodeFormatItem }
            //枚举BarcodeFormat
            BarcodeFormat.values().forEach {
                BarcodeFormatItem()() {
                    itemContent = barcodeContent
                    itemBarcodeFormat = it
                }
            }
            //用字符串创建BarcodeFormat
            // BarcodeFormat.valueOf("CODE_128")
        }
    }


}