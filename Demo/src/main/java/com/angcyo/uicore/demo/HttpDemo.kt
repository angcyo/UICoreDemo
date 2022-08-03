package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.http.base.jsonObject
import com.angcyo.http.post
import com.angcyo.http.rx.ToastObserver
import com.angcyo.http.rx.observer
import com.angcyo.library.L
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.base.string
import com.google.gson.JsonElement
import retrofit2.Response

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/03
 */
class HttpDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.item_http_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val editText = itemHolder.ev(R.id.edit_text)
                val textView = itemHolder.tv(R.id.text_view)

                editText?.setInputText("http://121.36.149.100:9009/pecker-web/login")
                itemHolder.click(R.id.request_button) {
                    val url = editText.string(true)
                    if (url.isNotEmpty()) {
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
            }
        }
    }

}