package com.angcyo.uicore.demo.dslitem

import android.graphics.Bitmap
import android.graphics.Color
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.L
import com.angcyo.library.ex.toStr
import com.angcyo.qrcode.createBarcode
import com.angcyo.rcode.RCode
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.google.zxing.BarcodeFormat

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/09/09
 */
class BarcodeFormatItem : DslAdapterItem() {

    /**条码内容*/
    var itemContent: String? = null
        set(value) {
            field = value
            itemBitmap = null
        }

    /**条形码格式*/
    var itemBarcodeFormat: BarcodeFormat? = null
        set(value) {
            field = value
            itemBitmap = null
        }

    private var itemBitmap: Bitmap? = null

    init {
        itemLayoutId = R.layout.barcode_format_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.tv(R.id.lib_text_view)?.text = itemBarcodeFormat?.toStr()

        if (itemBitmap == null) {
            L.i("生成条码:$itemContent $itemBarcodeFormat ↓")
            RCode.HINTS.clear() //清空配置
            itemBitmap = createBarcode(
                itemContent ?: "",
                400,
                backgroundColor = Color.GRAY,
                format = itemBarcodeFormat ?: BarcodeFormat.CODE_128
            )
            //itemContent?.createQRCode(400, format = itemBarcodeFormat ?: BarcodeFormat.QR_CODE)
        }
        itemHolder.img(R.id.lib_image_view)?.apply {
            if (itemBitmap == null) {
                setImageResource(R.drawable.core_help_svg)
            } else {
                setImageBitmap(itemBitmap)
            }
        }
    }

}