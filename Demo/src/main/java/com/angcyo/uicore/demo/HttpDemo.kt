package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.http.base.jsonObject
import com.angcyo.http.post
import com.angcyo.http.rx.ToastObserver
import com.angcyo.http.rx.observer
import com.angcyo.item.DslGridMediaItem
import com.angcyo.item.style.addGridMedia
import com.angcyo.item.style.gridMediaSpanCount
import com.angcyo.library.L
import com.angcyo.library.ex._colorDrawable
import com.angcyo.pager.dslitem.DslNineMediaItem
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

            //media
            DslGridMediaItem()() {
                itemBackgroundDrawable = _colorDrawable(Color.RED)
                addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/47f9c14b-813b-d697-cadb-f0f0d99d46af.png")
            }
            DslNineMediaItem()() {
                itemBackgroundDrawable = _colorDrawable(Color.BLUE)
                addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/bd7d94fc-ef75-a48c-fb2a-db3a8d3b567d.png")
            }
            DslNineMediaItem()() {
                itemBackgroundDrawable = _colorDrawable(Color.RED)
                addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/bd7d94fc-ef75-a48c-fb2a-db3a8d3b567d.png")
                addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/d446f1f3-9227-fe64-543c-93e3dad669fe.png")
            }
            DslNineMediaItem()() {
                itemBackgroundDrawable = _colorDrawable(Color.RED)
                addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/bd7d94fc-ef75-a48c-fb2a-db3a8d3b567d.png")
                addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/47f9c14b-813b-d697-cadb-f0f0d99d46af.png")
                addGridMedia("http://laserpecker.oss-cn-shenzhen.aliyuncs.com/app/images/4b7a140c-7ff7-550b-bab6-55967dcdcdde.png")
                gridMediaSpanCount = 3
            }
        }
    }

}