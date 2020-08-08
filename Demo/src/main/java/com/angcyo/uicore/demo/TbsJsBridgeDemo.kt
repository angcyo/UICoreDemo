package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.http.base.jsonString
import com.angcyo.library.L
import com.angcyo.library.ex.className
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.toUri
import com.angcyo.library.toastQQ
import com.angcyo.tbs.core.TbsWebFragment
import com.hjhrq1991.library.tbs.BridgeHandler
import com.hjhrq1991.library.tbs.CallBackFunction

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/08/08
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class TbsJsBridgeDemo : TbsWebFragment() {
    init {
        contentLayoutId = R.layout.layout_tbs_js_bridge_demo
        fragmentTitle = TbsJsBridgeDemo::class.java.className()
        webConfig.uri = "file:///android_asset/tbs_js_bridge_demo.html".toUri()
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.click(R.id.test1) {
            _tbsWebView?.callHandler(
                "functionInJs",
                jsonString {
                    add("code", 200)
                    add("data", "测试数据:${nowTimeString()}")
                },
                object : CallBackFunction {
                    override fun onCallBack(data: String?) {
                        L.d("onCallBack: $data")
                    }
                })
        }

        _vh.click(R.id.test2) {
            _tbsWebView?.send("hello")
        }
    }

    override fun onCreateViewAfter(savedInstanceState: Bundle?) {
        super.onCreateViewAfter(savedInstanceState)
        _tbsWebView?.apply {
//            addJavascriptInterface(
//                MainJavascriptInterface(null, _tbsWebView),
//                "android"
//            )
            //addJavascriptInterface(TbsJavascriptInterface(), "android")

            registerHandler("submitFromWeb", object : BridgeHandler {
                override fun handler(data: String?, function: CallBackFunction?) {
                    L.i(data, "\n", function)
                    function?.onCallBack("result form java")

                    toastQQ(data)
                }
            })
        }
    }
}