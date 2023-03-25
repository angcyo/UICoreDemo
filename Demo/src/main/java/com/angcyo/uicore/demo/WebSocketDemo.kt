package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.bindItem
import com.angcyo.http.rx.doBack
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemText
import com.angcyo.library.component.NetUtils
import com.angcyo.library.ex._color
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.toSizeString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.websocket.WSClient
import com.angcyo.websocket.WSClientModel
import com.angcyo.websocket.WSServer
import com.angcyo.websocket.WSServerModel
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/03/24
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class WebSocketDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_web_socket_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.hawkInstallAndRestore("WebSocket_")

                itemHolder.tv(R.id.tip_text_view)?.text = "${NetUtils.localIPAddress}"
                //itemHolder.tv(R.id.result_text_view)?.text = "${NetUtils.localIPAddress}"

                itemHolder.click(R.id.connect_button) {
                    startClientTest(itemHolder)
                }
                itemHolder.click(R.id.disconnect_button) {
                    wsClient?.close()
                    wsClient = null
                }
                itemHolder.click(R.id.start_button) {
                    startServerTest(itemHolder)
                }
                itemHolder.click(R.id.client_send_button) {
                    try {
                        val message = itemHolder.tv(R.id.content_edit).string()
                        renderTextItem("C发送:${message}")
                        wsClient?.send(message)
                    } catch (e: Exception) {
                        renderTextItem("$e")
                    }
                }
                itemHolder.click(R.id.server_send_button) {
                    try {
                        val message = itemHolder.tv(R.id.content_edit).string()
                        renderTextItem("S发送:${message}")
                        wsServer?.sendMessage(message)
                    } catch (e: Exception) {
                        renderTextItem("$e")
                    }
                }
                itemHolder.click(R.id.send_1_button) {
                    clientSendBytesKb(1)
                }
                itemHolder.click(R.id.send_5_button) {
                    clientSendBytesMb(5)
                }
                itemHolder.click(R.id.send_10_button) {
                    clientSendBytesMb(10)
                }
                itemHolder.click(R.id.send_20_button) {
                    clientSendBytesMb(20)
                }
                itemHolder.click(R.id.send_100_button) {
                    clientSendBytesMb(100)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wsClient?.close()
        wsServer?.stop()
    }

    fun renderTextItem(text: CharSequence?) {
        _adapter.render {
            DslTextItem()() {
                itemText = span {
                    append(nowTimeString()) {
                        foregroundColor = _color(R.color.text_general_color)
                    }
                    appendln()
                    append(text) {
                        foregroundColor = _color(R.color.text_sub_color)
                    }
                }
            }
        }
    }

    fun clientSendBytesKb(num: Int) {
        doBack {
            try {
                val bytes = ByteArray(num * 1024)
                renderTextItem("C发送:${bytes.size.toSizeString()}")
                wsClient?.send(bytes)
            } catch (e: Exception) {
                renderTextItem("$e")
            }
        }
    }

    fun clientSendBytesMb(num: Int) {
        doBack {
            try {
                val bytes = ByteArray(num * 1024 * 1024)
                renderTextItem("C发送:${bytes.size.toSizeString()}")
                wsClient?.send(bytes)
            } catch (e: Exception) {
                renderTextItem("$e")
            }
        }
    }

    //region ---client---

    var wsClient: WSClient? = null

    fun startClientTest(vh: DslViewHolder) {
        if (wsClient != null) return
        vmApp<WSClientModel>().apply {
            serverStateData.observe {
                it?.let {
                    vh.tv(R.id.result_text_view)?.text = "状态:${it}"
                }
            }
            serverMessageData.observe {
                it?.let {
                    renderTextItem("收到服务端消息:${it.bytes?.size?.toSizeString() ?: it.message}")
                }
            }
        }
        wsClient = WSClient(vh.tv(R.id.address_edit).string()).apply {
            connect()
        }
    }

    //endregion ---client---

    //region ---server---

    var wsServer: WSServer? = null

    fun startServerTest(vh: DslViewHolder) {
        if (wsServer != null) return
        vmApp<WSServerModel>().apply {
            clientConnectData.observe {
                it?.let {
                    renderTextItem("客户端连接:${it.client} ${it.handshake}")
                }
            }
            clientMessageData.observe {
                it?.let {
                    renderTextItem("收到客户端消息:${it.bytes?.size?.toSizeString() ?: it.message}")
                }
            }
        }
        wsServer = WSServer().apply {
            start()
            vh.tv(R.id.tip_text_view)?.text = "已启动本地服务:${serverAddress}"
            vh.tv(R.id.address_edit)?.text = serverAddress
        }
    }

    //endregion ---server---
}