package com.angcyo.uicore.demo.p2p

import com.angcyo.device.bean.DeviceBean
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.http.asRequestBody
import com.angcyo.http.base.readString
import com.angcyo.http.interceptor.LogInterceptor
import com.angcyo.http.post2Body
import com.angcyo.http.progress.toProgressBody
import com.angcyo.http.rx.observe
import com.angcyo.library.ex.nowTime
import com.angcyo.library.ex.size
import com.angcyo.library.ex.toMsTime
import com.angcyo.library.ex.toSizeString
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span
import java.io.ByteArrayInputStream

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/12/02
 */
class AppUdpDeviceItem : DslAdapterItem() {

    /**数据*/
    var itemDeviceBean: DeviceBean? = null

    init {
        itemLayoutId = R.layout.app_udp_device_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.lib_text_view)?.text = itemDeviceBean?.name
        itemHolder.tv(R.id.lib_des_view)?.text = span {
            itemDeviceBean?.apply {
                deviceId?.let {
                    append("id:${it}")
                }
                appendln()
                append("${address}:$port")
                appendln()
                token?.let {
                    append("token:$it")
                }
            }
        }

        //send
        itemHolder.click(R.id.send_5_button) {
            sendBytes(itemHolder, 5 * 1024 * 1024)
        }
        itemHolder.click(R.id.send_10_button) {
            sendBytes(itemHolder, 10 * 1024 * 1024)
        }
        itemHolder.click(R.id.send_100_button) {
            sendBytes(itemHolder, 100 * 1024 * 1024)
        }

        //device
        itemHolder.click(R.id.device_button) {
            request(itemHolder, "/device")
        }

        //test
        itemHolder.click(R.id.test_button) {
            request(itemHolder, "/")
        }
    }

    /**发送请求*/
    fun request(itemHolder: DslViewHolder, url: String?) {
        itemDeviceBean?.apply {
            post2Body {
                codeKey = null
                this.url = toApi(url)
            }.observe { data, error ->
                data?.let {
                    result(itemHolder, it.body().readString())
                }
                error?.let {
                    result(itemHolder, it.message)
                }
            }
        }
    }

    /**返回值显示在界面上*/
    fun result(itemHolder: DslViewHolder, text: CharSequence?) {
        itemHolder.tv(R.id.lib_result_view)?.text = text
    }

    /**发送字节测试
     * [bytesSize] 需要发送的字节大小 100mb*/
    fun sendBytes(itemHolder: DslViewHolder, bytesSize: Int) {
        var progressText = ""
        itemDeviceBean?.apply {
            val bytes = ByteArray(bytesSize)
            val inputStream = ByteArrayInputStream(bytes)
            post2Body {
                codeKey = null
                this.url = toApi("/bytes")
                header = hashMapOf(LogInterceptor.closeLog(true))

                val startTime = nowTime()
                /* requestBody = bytes.toRequestBody("application/octet-stream".toMediaType())
                     .toProgressBody { progressInfo, exception ->
                         progressInfo?.let {
                             val nowTime = nowTime()
                             progressText =
                                 "${it.percent}% ${it.speed} bytes/s ${(nowTime - startTime).toMsTime()}"
                             //L.w(text)
                             result(itemHolder, progressText)
                         }
                     }*/
                requestBody = inputStream.asRequestBody(bytes.size().toLong())
                    .toProgressBody { progressInfo, exception ->
                        progressInfo?.let {
                            val nowTime = nowTime()
                            progressText =
                                "${it.percent}% ${it.speed.toSizeString()}/s ${(nowTime - startTime).toMsTime()}"
                            //L.w(text)
                            result(itemHolder, progressText)
                        }
                    }
            }.observe { data, error ->
                data?.let {
                    result(itemHolder, "${progressText}\n${it.body().readString()}")
                }
                error?.let {
                    result(itemHolder, it.message)
                }
            }
        }
    }

}