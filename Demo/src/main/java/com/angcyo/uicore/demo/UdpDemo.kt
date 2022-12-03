package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.vmApp
import com.angcyo.device.DeviceServerHelper
import com.angcyo.device.client.DeviceDiscoverModel
import com.angcyo.dsladapter.bindItem
import com.angcyo.http.rx.doBack
import com.angcyo.http.rx.doMain
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.getWifiIP
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.size
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.p2p.AppUdpDeviceItem
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.Charset

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/12/01
 */
class UdpDemo : AppDslFragment() {

    /**广播的端口*/
    var broadcastPort = 9999

    init {
        fragmentTitle = span {
            appendLine("UdpDemo")
            append(getWifiIP()) {
                fontSize = 12 * dpi
            }
        }
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_udp_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.click(R.id.broadcast_button) {
                    broadcastPort =
                        itemHolder.tv(R.id.port_edit).string().toIntOrNull() ?: broadcastPort
                    doBack {
                        sendBroadcast(itemHolder)
                    }
                }
                itemHolder.click(R.id.broadcast_listener_button) {
                    broadcastPort =
                        itemHolder.tv(R.id.port_edit).string().toIntOrNull() ?: broadcastPort
                    doBack {
                        listenerBroadcast(itemHolder)
                    }
                }
                itemHolder.click(R.id.device_server_button) {
                    DeviceServerHelper.startServer()
                }
                itemHolder.click(R.id.device_search_button) {
                    DeviceServerHelper.startDiscoverServer()
                }
            }
        }

        //监听设备发现
        vmApp<DeviceDiscoverModel>().apply {

            //以前的数据
            _adapter.renderFooter {
                for (device in deviceList) {
                    AppUdpDeviceItem()() {
                        itemDeviceBean = device
                    }
                }
            }

            //监听新数据
            deviceFoundData.observe {
                it?.let { bean ->
                    _adapter.renderFooter {
                        AppUdpDeviceItem()() {
                            itemDeviceBean = bean
                        }
                    }
                }
            }

        }
    }

    fun Appendable.appendAddress(socket: DatagramSocket) {
        appendln()
        append("local:${socket.localAddress}")
        appendln()
        append("localSocket:${socket.localSocketAddress}")
        appendln()
        append("remoteSocket:${socket.remoteSocketAddress}")
        appendln()
    }

    fun Appendable.appendAddress(packet: DatagramPacket) {
        appendln()
        append("address:${packet.address}")
        appendln()
        append("socketAddress:${packet.socketAddress}")
        appendln()
        append("port:${packet.port} offset:${packet.offset} length:${packet.length}")
        appendln()
    }

    /**发送UDP广播*/
    fun sendBroadcast(itemHolder: DslViewHolder) {
        try {
            val host = itemHolder.tv(R.id.broadcast_address_edit).string()
            val socket = DatagramSocket()
            doMain {
                itemHolder.tv(R.id.result_text_view)?.text = span {
                    append("发送广播:$host")
                    appendAddress(socket)
                }
            }

            val address = InetAddress.getByName(host) //广播的地址
            val content = "${nowTimeString()}:${itemHolder.tv(R.id.content_edit).string()}"
            val bytes = content.toByteArray()
            val data = DatagramPacket(bytes, 0, bytes.size(), address, broadcastPort)//数据包
            socket.send(data)//发送UDP数据包

            doMain {
                itemHolder.tv(R.id.result_text_view)?.text = span {
                    append("发送广播结束:$host")
                    appendln()
                    append("$address")
                    appendAddress(socket)
                    appendAddress(data)
                    append("内容:${content}")
                }

                socket.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()

            doMain {
                itemHolder.tv(R.id.result_text_view)?.text = e.message
            }
        }
    }

    /**监听UDP广播*/
    fun listenerBroadcast(itemHolder: DslViewHolder) {
        try {
            val socket = DatagramSocket(broadcastPort)
            val buffer = ByteArray(1024)
            val data = DatagramPacket(buffer, buffer.size())//数据包存放

            doMain {
                itemHolder.tv(R.id.result_text_view)?.text = span {
                    append("监听广播:${nowTimeString()}")
                    appendAddress(socket)
                }
            }

            socket.receive(data)//接收UDP数据

            doMain {
                itemHolder.tv(R.id.result_text_view)?.text = span {
                    append("接收广播:${buffer.toString(Charset.defaultCharset())}")
                    appendAddress(socket)
                    appendAddress(data)
                }

                socket.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()

            doMain {
                itemHolder.tv(R.id.result_text_view)?.text = e.message
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DeviceServerHelper.stopServer()
        DeviceServerHelper.stopDiscoverServer()
    }

}