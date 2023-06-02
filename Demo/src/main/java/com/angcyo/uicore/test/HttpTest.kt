package com.angcyo.uicore.test

import com.angcyo.coroutine.launchGlobal
import com.angcyo.http.base.jsonObject
import com.angcyo.http.base.readString
import com.angcyo.http.base.toTextBody
import com.angcyo.http.get
import com.angcyo.http.get2Body
import com.angcyo.http.post
import com.angcyo.http.rsa.aesEncrypt
import com.angcyo.http.rx.BaseObserver
import com.angcyo.http.rx.observer
import com.angcyo.library.L
import com.angcyo.library.annotation.TestPoint
import com.angcyo.library.ex.md5
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

    /**2023-6-2*/
    fun test1() {
        post {
            url = "http://192.168.31.13:9009/pecker-web/app/log/getOperationLogPageInfo"
            val key = "wba2022041620221"
            val json = jsonObject {
                add("a", 1)
                add("b", "2.2")
                add("c", 3.3)
            }
            val body = json.toString()
            val bodyAes = body.aesEncrypt(key)!!
            val encryption = (bodyAes + key).md5()!!
            header =
                hashMapOf("token" to "68b366a5ad56418497235c465a1132b8", "encryption" to encryption)
            requestBody = bodyAes.toTextBody()
            isSuccessful = {
                it.isSuccessful
            }
        }.observer {
            onObserverEnd = { data, error ->
                L.i(error)
                L.i(data)
            }
        }
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