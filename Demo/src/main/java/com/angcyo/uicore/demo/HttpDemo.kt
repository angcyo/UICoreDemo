package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.core.component.fileSelector
import com.angcyo.core.coreApp
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.bindItem
import com.angcyo.http.base.jsonObject
import com.angcyo.http.form.uploadFile
import com.angcyo.http.post
import com.angcyo.http.rx.ToastObserver
import com.angcyo.http.rx.doMain
import com.angcyo.http.rx.observer
import com.angcyo.item.DslGridMediaItem
import com.angcyo.item.style.addGridMedia
import com.angcyo.item.style.gridMediaSpanCount
import com.angcyo.library.L
import com.angcyo.library.ex._colorDrawable
import com.angcyo.library.ex.elseNull
import com.angcyo.library.ex.loadUrl
import com.angcyo.library.ex.nowTimeString
import com.angcyo.pager.dslitem.DslNineMediaItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.websocket.WSServer
import com.angcyo.websocket.WSServerModel
import com.angcyo.websocket.service.bindLogWSServer
import com.angcyo.widget.DslButton
import com.angcyo.widget.DslLoadingButton
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.base.string
import com.google.gson.JsonElement
import retrofit2.Response

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/03
 */
class HttpDemo : AppDslFragment() {

    var wsServer: WSServer? = null

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        //WebSocket监听
        vmApp<WSServerModel>().apply {
            clientConnectData.observe {
                it?.let {
                    it.server.sendMessage("连接成功:${nowTimeString()}->${it.server.serverAddress}")
                }
            }
            clientMessageData.observe {
                it?.let {
                    it.clientInfo.server.sendMessage("收到[${it.clientInfo.client.remoteSocketAddress}]消息:${nowTimeString()}->${it.message}")
                }
            }
        }

        renderDslAdapter {
            bindItem(R.layout.item_http_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val editText = itemHolder.ev(R.id.edit_text)
                val textView = itemHolder.tv(R.id.text_view)

                editText?.setInputText("http://121.36.149.100:9009/pecker-web/login")
                itemHolder.click(R.id.request_button) {
                    val url = editText.string(true)
                    if (url.isNotEmpty()) {
                        (it as? DslButton)?.showButtonLoading = true
                        post {
                            this.url = url
                            msgKey = "errMsg"
                            body = jsonObject {
                                add("userName", "18888888888")
                                add("password", "123456")
                                add("uuid", "123456")
                                add("code", "xxx")
                            }
                        }.observer(ToastObserver<Response<JsonElement>>().apply {
                            onObserverEnd = { data, error ->
                                (it as? DslButton)?.showButtonLoading = false

                                /*loginEntity.value = data?.toBean<LoginEntity>()?.apply {
                                    user?.let {
                                        userEntity.target = it
                                    }
                                    coreBoxOf(LoginEntity::class.java).put(this)
                                }*/
                                L.i(data, error)

                                textView?.text = data?.toString()
                            }
                        })
                    }
                }

                //上传文件
                itemHolder.click(R.id.request_loading_button) { view ->
                    (view as? DslLoadingButton)?.isLoading = true
                    fileSelector {
                        it?.run {
                            fileUri.loadUrl()
                                ?.uploadFile("https://server.hingin.com/pecker-web/oss/upload", {
                                    enablePassCheck = false
                                    configMultipartBody = {
                                        addFormDataPart("type", "8")
                                    }
                                }) {
                                    L.i(it)
                                    (view as? DslLoadingButton)?.isLoading = false
                                }
                        }.elseNull {
                            (view as? DslLoadingButton)?.isLoading = false
                        }
                    }
                }

                //WebSocket
                itemHolder.click(R.id.websocket_button) {
                    WSServer.bindWebSocketServer(this@HttpDemo) {
                        wsServer = it
                        doMain {
                            itemHolder.tv(R.id.websocket_button)?.text = it.serverAddress
                        }
                    }
                }

                //log
                itemHolder.click(R.id.log_button) {
                    coreApp().bindLogWSServer()
                }
            }

            //media
            for (i in 0..30) {
                DslGridMediaItem()() {
                    itemBackgroundDrawable = _colorDrawable(Color.RED)
                    addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/47f9c14b-813b-d697-cadb-f0f0d99d46af.png")
                }
                DslNineMediaItem()() {
                    itemLayoutId = R.layout.item_square_layout
                    itemBackgroundDrawable = _colorDrawable(Color.BLUE)
                    addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/bd7d94fc-ef75-a48c-fb2a-db3a8d3b567d.png")
                }
                DslNineMediaItem()() {
                    itemLayoutId = R.layout.item_square_layout
                    itemBackgroundDrawable = _colorDrawable(Color.GREEN)
                    addGridMedia("https://laserpecker-prod.oss-cn-hongkong.aliyuncs.com/app/images/319bc400-9f1b-28ef-2f1f-b44bb9a97ccb.png?w=12000&h=6008&")
                }
                DslNineMediaItem()() {
                    itemLayoutId = R.layout.item_square_layout
                    itemBackgroundDrawable = _colorDrawable(Color.RED)
                    addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/bd7d94fc-ef75-a48c-fb2a-db3a8d3b567d.png")
                    addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/d446f1f3-9227-fe64-543c-93e3dad669fe.png")
                }
                DslNineMediaItem()() {
                    itemLayoutId = R.layout.item_square_layout
                    itemBackgroundDrawable = _colorDrawable(Color.MAGENTA)
                    addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/bd7d94fc-ef75-a48c-fb2a-db3a8d3b567d.png")
                    addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/47f9c14b-813b-d697-cadb-f0f0d99d46af.png")
                    addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/4b7a140c-7ff7-550b-bab6-55967dcdcdde.png")
                    gridMediaSpanCount = 3
                }
                DslNineMediaItem()() {
                    itemLayoutId = R.layout.item_square_layout
                    //empty
                }
            }
        }
    }

}