package com.angcyo.uicore.demo

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import com.angcyo.base.dslAHelper
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.getData
import com.angcyo.library.ex.copy
import com.angcyo.library.ex.save
import com.angcyo.library.toast
import com.angcyo.pager.dslPager
import com.angcyo.qrcode.dslCode
import com.angcyo.rcode.RCode
import com.angcyo.tbs.open
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.string
import com.angcyo.widget.recycler.get

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/28
 */

class QrCodeDemo : AppDslFragment() {

    /**外部传过来的数据*/
    var codeResult: String? = null

    /**创建的二维码保存路径*/
    var codeSavePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeResult = getData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDslAdapter {
            DslAdapterItem()() {
                itemLayoutId = R.layout.item_qr_code_demo
                itemBindOverride = { itemHolder, _, _, _ ->

                    itemHolder.focus<View>(R.id.scan_button)

                    //扫一扫
                    itemHolder.click(R.id.scan_button) {
                        dslCode(activity) {
                            onResult = {
                                itemHolder.tv(R.id.text_view)?.text = it ?: "已取消"
                            }
                        }
                    }

                    //创建二维码
                    itemHolder.click(R.id.create_qrcode) {
                        //创建二维码
                        AsyncTask.execute {
                            val time = System.currentTimeMillis()
                            val bitmap = RCode.syncEncodeQRCode(
                                itemHolder.tv(R.id.edit_text)?.string(),
                                500
                            )
                            activity?.runOnUiThread {
                                itemHolder.img(R.id.image_view)?.setImageBitmap(bitmap)

                                if (codeResult.isNullOrBlank()) {
                                    itemHolder.tv(R.id.text_view)?.text =
                                        "${itemHolder.tv(R.id.text_view)?.text}\n创建二维码耗时:${System.currentTimeMillis() - time}ms"
                                }

                                codeResult = null
                            }
                            AsyncTask.execute {
                                codeSavePath = bitmap?.save()?.absolutePath
                            }
                        }
                    }

                    //复制内容
                    itemHolder.click(R.id.text_view) {
                        itemHolder.tv(R.id.text_view)?.text?.apply {
                            copy()
                            toast(
                                "已复制:$this",
                                fContext(),
                                R.drawable.lib_ic_info,
                                R.layout.lib_toast_qq_layout
                            )

                            dslAHelper {
                                open(this@apply.toString())
                            }
                        }
                    }

                    //大图浏览
                    itemHolder.click(R.id.image_view) {
                        codeSavePath?.run {
                            dslPager {
                                fromView = it
                                addMedia(codeSavePath)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)

        _vh.post {
            _recycler[0]?.apply {
                codeResult?.run { tv(R.id.text_view)?.text = this }
                clickView(R.id.create_qrcode)
            }
        }
    }
}