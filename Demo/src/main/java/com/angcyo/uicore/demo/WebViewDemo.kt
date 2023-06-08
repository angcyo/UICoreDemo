package com.angcyo.uicore.demo

import android.webkit.JavascriptInterface
import androidx.annotation.Keep
import com.angcyo.library.L
import com.angcyo.library.ex.toUri
import com.angcyo.library.toastQQ
import com.angcyo.library.utils.UrlParse
import com.angcyo.web.WebFragment
import com.angcyo.web.core.DslWebView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/06/08
 */
class WebViewDemo : WebFragment() {

    init {
        webConfig.uri = "http://192.168.31.171:65050/?l=zh-CN".toUri()
//        webConfig.uri = "https://www.baidu.com/".toUri()
    }

    override fun onInitWebView(webView: DslWebView) {
        webView.addJavascriptInterface(Logger(), "logger")

        webView.shouldOverrideUrlLoadAction = { webView, url ->
            //hingin://laserpecker.com?t=ad8cec416fa84a13b089c44d661e3c56
            val url = url?.lowercase()
            if (url?.startsWith("hingin://laserpecker.com") == true) {
                val token = UrlParse.getUrlParams(url)["t"]
                toastQQ("登录成功\n$token")
                close()
                true
            } else {
                false
            }
        }
    }
}

@Keep
class Logger {

    @JavascriptInterface
    fun info(msg: Array<String?>?) {
        L.i(msg)
    }

    @JavascriptInterface
    fun error(msg: Array<String?>?) {
        L.e(msg)
    }
}