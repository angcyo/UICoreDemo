package com.angcyo.uicore.test

import android.graphics.RectF
import com.angcyo.library.Library
import com.angcyo.library.annotation.TestPoint
import com.angcyo.library.ex.limitInRect
import com.orhanobut.hawk.HawkValueParserHelper

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/06/02
 */
object Test {

    @TestPoint
    fun test() {
        //val number = EngraveTransitionManager.generateEngraveIndex()
        //val b1 = number.toByteArray(4)
        //val b2 = "$number".toByteArray().trimAndPad(4)

        val limit = RectF(15f, 15f, 25f, 25f)
        val rect = RectF(10f, 10f, 25f, 30f)
        rect.limitInRect(limit)
        val path = Library.hawkPath
        val value = HawkValueParserHelper.parse(
            "KEY_COMPLIANCE_STATE_1",
            "java.lang.String##0V@AQLf4+PnoAYmLEofEnsL5bXhT3D/UTk5WUYjVN17gWIAmg4P"
        )

        /*thread {
                       Ping.ping("www.baidu.com") {

                       }
                       Ping.ping("www.google.com") {

                       }
                   }*/

        //LaserPeckerConfigHelper.init()

        /*TcpSend().apply {
            address = "192.168.31.88"//"www.baidu.com"
            port = 8080
            sendBytes = "123".toByteArray()
            onSendAction = { receiveBytes, error ->
                receiveBytes?.let {
                    L.i("TCP收到字节:${it.size} :${it.toString(Charsets.UTF_8)}")
                }
            }
            startSend()
        }*/

        /*val context = "{\"pageNo\":1,\"pageSize\":10}"
        val encrypt = AESEncrypt.encrypt(context, "wba2022041620221", "123456")
        val decrypt = AESEncrypt.decrypt(encrypt, "wba2022041620221", "123456")
        println(encrypt)
        println(decrypt)*/
    }

}