package com.angcyo.uicore.test

import android.graphics.PointF
import com.angcyo.library.L
import com.angcyo.library.annotation.TestPoint
import com.angcyo.library.component.parser.parseDateTemplate
import com.angcyo.library.component.pool.acquireTempMatrix
import com.angcyo.library.component.pool.acquireTempPointF
import com.angcyo.library.component.pool.release
import com.angcyo.library.ex.angle
import com.angcyo.library.ex.mapPoint
import com.angcyo.library.ex.rotate
import kotlin.math.atan
import kotlin.math.tan

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

        /*val limit = RectF(15f, 15f, 25f, 25f)
        val rect = RectF(10f, 10f, 25f, 30f)
        rect.limitInRect(limit)
        val path = Library.hawkPath
        val value = HawkValueParserHelper.parse(
            "KEY_COMPLIANCE_STATE_1",
            "java.lang.String##0V@AQLf4+PnoAYmLEofEnsL5bXhT3D/UTk5WUYjVN17gWIAmg4P"
        )*/

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

        //V8Engine.test()
        //QuickJSEngine.test()

        //testPoint()
        //testTemplateParse()
    }

    private fun testTemplateParse() {
        val result =
            "[[YYYYescape] [[angcyo]] YYYY-MM-DDTHH:mm:ss:SS Z[Z] d dd ZZ A a".parseDateTemplate()
        L.d(result)
    }

    private fun testPoint() {
        val p1 = PointF(10f, 10f)
        val p2 = PointF(20f, 20f)
        //val p2 = PointF(20f, 20f)

        //计算2个点之间的角度
        val a1 = p1.angle(p2)
        //val a2 = p2.angle(p1)

        val width = 0.1f
        val stepHeight = 0.04f

        val rotateAngle = 90f - a1
        val reverseMatrix = acquireTempMatrix()
        reverseMatrix.setRotate(-rotateAngle, p1.x, p1.y)
        p2.rotate(rotateAngle, p1.x, p1.y)

        val startX = p1.x - width / 2
        val startY = p1.y
        val endX = p2.x + width / 2
        val endY = p2.y

        val angle = Math.toDegrees(atan(stepHeight / width).toDouble())

        //使用指定角度的线填充矩形
        var x = startX
        var y = startY
        val builder = StringBuilder()
        val tempPointF = acquireTempPointF()
        while (y <= endY) {
            tempPointF.set(x, y)
            reverseMatrix.mapPoint(tempPointF)

            if (x == startX && y == startY) {
                //第一个点
                builder.appendLine("G0X${tempPointF.x}Y${tempPointF.y}")
                x = endX
                continue
            }
            builder.appendLine("G1X${tempPointF.x}Y${tempPointF.y}")

            if (x >= endX) {
                x = startX
            } else {
                x = endX
            }
            y += tan(Math.toRadians(angle)).toFloat() * width
        }
        tempPointF.release()
        reverseMatrix.release()
        L.i(builder)
    }
}