package com.angcyo.uicore.demo

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.copy
import com.angcyo.library.toast
import com.angcyo.qrcode.dslCode
import com.angcyo.rcode.RCode
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.recycler.get

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/28
 */

class QrCodeDemo : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDslAdapter {
            DslAdapterItem()() {
                itemLayoutId = R.layout.item_qr_code_demo
                itemBindOverride = { itemHolder, _, _, _ ->
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
                                itemHolder.tv(R.id.edit_text)?.toString(),
                                500
                            )
                            activity?.runOnUiThread {
                                itemHolder.img(R.id.image_view)?.setImageBitmap(bitmap)
                                itemHolder.tv(R.id.text_view)?.text =
                                    "创建二维码耗时:${System.currentTimeMillis() - time}ms"
                            }
                        }
                    }

                    //复制内容
                    itemHolder.click(R.id.text_view) {
                        itemHolder.tv(R.id.text_view)?.text?.run {
                            copy()
                            toast("已复制:$this", R.drawable.lib_ic_info, R.layout.lib_qq_toast_layout)
                        }
                    }
                }
            }
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)

        _adapter.onDispatchUpdates {
            _vh.post {
                _recycler[0]?.clickView(R.id.create_qrcode)
            }
        }
    }
}