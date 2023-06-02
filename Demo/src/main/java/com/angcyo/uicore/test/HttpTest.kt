package com.angcyo.uicore.test

import com.angcyo.coroutine.launchGlobal
import com.angcyo.http.base.readString
import com.angcyo.http.get
import com.angcyo.http.get2Body
import com.angcyo.http.rx.BaseObserver
import com.angcyo.library.L
import com.angcyo.library.annotation.TestPoint
import com.angcyo.uicore.App

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/17
 */

object HttpTest {

    @TestPoint
    fun test() {

    }

}

fun testHttp() {
    //testGet()
    //testCoroutineHttp()
}

fun testGet() {
    get {
        url = "https://www.baidu.com/"
    }.subscribe(BaseObserver())
}

fun testCoroutineHttp() {
    launchGlobal {
        val url1 = "https://www.mxnzp.com/api/tools/system/time"
        val url2 = "https://www.baidu.com/"
        try {
            L.d("开始请求1:$url1")
            val result1 = url1.get(headerMap = App.headerMap)
            L.i("返回1:${result1?.body()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            L.d("开始请求2:$url2")
            val result2 = url2.get()
            L.i("返回2:${result2?.body()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            L.d("开始请求3:$url2")
            val result3 = url2.get2Body()
            L.i("返回3:${result3?.body()?.readString()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class HttpBean(
    var code: Int = -1,
    var msg: String? = null,
    var data: Any? = null
)