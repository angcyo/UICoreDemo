package com.angcyo.uicore.test

import android.graphics.Color
import android.graphics.Path
import android.graphics.PointF
import com.angcyo.library.L
import com.angcyo.library.annotation.TestPoint
import com.angcyo.library.component.parser.parseDateTemplate
import com.angcyo.library.component.pool.acquireTempMatrix
import com.angcyo.library.component.pool.acquireTempPointF
import com.angcyo.library.component.pool.release
import com.angcyo.library.ex.adjustDegrees
import com.angcyo.library.ex.angle
import com.angcyo.library.ex.eachPath
import com.angcyo.library.ex.mapPoint
import com.angcyo.library.ex.rotate
import com.angcyo.library.ex.toBitmap
import com.angcyo.library.ex.toDegrees
import com.angcyo.vector.toPathObj
import kotlin.math.absoluteValue
import kotlin.math.atan
import kotlin.math.tan

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/06/02
 */
@TestAnnotation("测试注解")
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
        //testSvg()
        //testAnnotation()
        //testEachPath()
    }

    private fun testEachPath() {
        val path = Path().apply {
            val r = 5f
            addCircle(r, r, r, Path.Direction.CW)
        }
        val path2 = Path().apply {
            val r = 100f
            addCircle(r, r, r, Path.Direction.CW)
        }

        //0 0.0 0 p:20.0,10.0 t:0.0,1.0
        //5843 0.9359128 0 p:19.203424,6.0888658 t:0.39111337,0.9203425
        //eachPath:0 0.0 0 p:20.0,10.0 t:0.0,1.0,90.0
        //eachPath:1 0.016018717 0 p:19.953806,10.960067 t:-0.09600665,0.9953807,95.50926
        //eachPath:2 0.032037433 0 p:19.809032,11.944964 t:-0.19449641,0.9809032,101.21531
        //eachPath:3 0.04805615 0 p:19.557974,12.94026 t:-0.29402602,0.95579743,107.099144

        val gcodeBuilder = StringBuilder()
        var lastRadians = 0f //弧度
        var prevRadians = 0f //前一个点的弧度
        var lastX = 0f
        var lastY = 0f
        val step = 1f //LibHawkKeys._pathAcceptableError
        listOf(path, path2).forEachIndexed { pathIndex, path ->
            lastRadians = 0f
            lastX = 0f
            lastY = 0f
            path.eachPath(step) { index, ratio, contourIndex, posArray, tanArray ->
                val radians = tanArray[2]//当前点的弧度
                L.i(
                    "eachPath:$index $ratio $contourIndex p:${posArray[0]},${posArray[1]} t:${tanArray[0]},${tanArray[1]} ${radians}:${
                        radians.toDegrees().adjustDegrees()
                    }:增量弧度:${radians - prevRadians} 增量角度:${(radians - prevRadians).absoluteValue.toDegrees()}"
                )
                prevRadians = radians
                if (index == 0) { //第一个点
                    gcodeBuilder.appendLine()
                    gcodeBuilder.appendLine("G0X${posArray[0]}Y${posArray[1]}")
                } else if (ratio == 1f) { //最后一个点
                    gcodeBuilder.appendLine("G1X${posArray[0]}Y${posArray[1]}")
                } else {
                    val dr = (radians - lastRadians).absoluteValue.toDegrees()
                    if (dr >= 10 /*LibHawkKeys.pathAcceptableRadians*/) { //超过10度, 则G1
                        gcodeBuilder.appendLine("G1X${lastX}Y${lastY}F1000")
                        lastRadians = radians
                    }
                }
                lastX = posArray[0]
                lastY = posArray[1]
            }
            L.w("Path:$pathIndex↓")
            L.w(gcodeBuilder)
        }
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

    private fun testSvg() {
        //svg <rect x="0.5" y="0.5" width="15" height="15" rx="3.5" fill="#FBCA37" stroke="white"/>
        //转成 pathData 数据M L C
        //val pathData = "M0.5,0.5L15.5,0.5L15.5,15.5L0.5,15.5L0.5,0.5Z"
        //rx ry 转换成 C

        val x = 0.5f
        val y = 0.5f
        val width = 15f
        val height = 15f
        val rx = 3.5f
        val ry = 3.5f
        //M L Q
        val pathData = buildString {
            append("M${x + rx},$y ")
            append("L${x + width - rx},$y ")
            append("Q${x + width},$y ${x + width},${y + ry} ")
            append("L${x + width},${y + height - ry} ")
            append("Q${x + width},${y + height} ${x + width - rx},${y + height} ")
            append("L${x + rx},${y + height} ")
            append("Q$x,${y + height} $x,${y + height - ry} ")
            append("L$x,${y + ry} ")
            append("Q$x,$y ${x + rx},$y ")
            append("Z")
        }
        //"M$x,$y L${x + width - rx},$y Q${x + width},$y ${x + width},$y${y + height - ry} L${x + width},${y + height} Q${x + width},${y + height} ${x + width},${y + height} L${x + rx},${y + height} Q$x,${y + height} $x,${y + height} L$x,${y + ry} Q$x,$y $x,$y Z"
        val bitmap = pathData.toPathObj().toBitmap(Color.RED)
        L.i(pathData)
    }

    private fun testAnnotation() {
        val annotation = Test::class.java.getAnnotation(TestAnnotation::class.java)
        L.i(annotation)
        if (annotation != null) {
            L.i(annotation.des)
        }
    }
}