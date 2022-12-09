package com.angcyo.uicore.demo

import com.angcyo.bluetooth.fsc.laserpacker.command.QueryCmd
import com.angcyo.canvas.utils.parseGCode
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.*
import com.angcyo.uicore.test.PathTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.pow

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testPath() {
        val ovalWidth = 100f
        //椭圆的高度
        val ovalHeight = 60f
        //测量矩形的宽度
        val rectWidth = 100f
        //测量矩形的高度
        val rectHeight = 100f

        val step = 2
        for (i in 0..40) {
            val oW = ovalWidth /*+ i * step*/
            val oH = ovalHeight + i * step
            val result = PathTest.maxRectInOval(oW, oH, rectWidth, rectHeight)

            //105.0 75.0 -> 61 61
            //104.0 74.0 -> 60 60

            //90.0 80.0 -> 59 59
            //90.0 82.0 -> 60 60
            //100.0 76.0 -> 60 60
            println("$oW $oH -> ${result.joinToString(" ")}")
        }
    }

    @Test
    fun testRegex() {
        val char: CharSequence = "今天是几号ˉ▽￣～切~~"
        val char2 = "几号"
        val char3 = "几号.*切"
        val char4 = "\\Q几号\\E" //包含
        val char5 = "\\Q$char\\E" //包含
        val char6 = "\\A$char\\z" //匹配字符串首尾
        val char7 = "\\A$char\\Z" //匹配字符串首尾, 包含新行
        val char8 = "^$char$" //匹配一行的首尾
        val char9 = "^是几$"

        println(char)
        println()
        println("匹配:${char2} ${char.contains(char2)}")
        println("正则匹配:${char2} ${char.contains(char2.toRegex())}")

        println()
        println("匹配:${char3} ${char.contains(char3)}")
        println("正则匹配:${char3} ${char.contains(char3.toRegex())}")

        println()
        println("匹配:${char4} ${char.contains(char4)}")
        println("正则匹配:${char4} ${char.contains(char4.toRegex())}")

        println()
        println("匹配:${char5} ${char.contains(char5)}")
        println("正则匹配:${char5} ${char.contains(char5.toRegex())}")

        println()
        println("匹配:${char6} ${char.contains(char6)}")
        println("正则匹配:${char6} ${char.contains(char6.toRegex())}")

        println()
        println("匹配:${char7} ${char.contains(char7)}")
        println("正则匹配:${char7} ${char.contains(char7.toRegex())}")

        println()
        println("匹配:${char8} ${char.contains(char8)}")
        println("正则匹配:${char8} ${char.contains(char8.toRegex())}")

        println()
        println("匹配:${char9} ${char.contains(char9)}")
        println("正则匹配:${char9} ${char.contains(char9.toRegex())}")
    }

    @Test
    fun testHex() {
        val text = "0123456789ABCDEF"//"angcyo"
        println(text.toHexByteArray())
        println(text.toHexByteArray().toHexString(true))
    }

    @Test
    fun testDirective() {
        for (int in 0 until 16) {
            print("$int->${int.toHexString(1)} ")
        }
        println()
        println(QueryCmd(0).toHexCommandString())
        println(QueryCmd(1).toHexCommandString())
        println(QueryCmd(2).toHexCommandString())
        println(QueryCmd(3).toHexCommandString())
        println("AA".padHexString(4))
        println("AA".padHexString(4, 1))
        println("AA".toInt(16))
        println("0101".toInt(16))
        println("0000F924".toInt(16))
        val byte: Byte = -86
        val bytes = byteArrayOf()
        //val bytes = byteArrayOf(-86, -69)
        println("${byte.toHexInt()} ${byte.toHexString().toInt(16)}")
        println("${bytes.toHexInt()}")
    }

    @Test
    fun testHex2() {
        val int = 123456
        val intHex = int.toHexString(8)
        val int2 = intHex.toHexByteArray().toHexInt()
        println(intHex)
        println("$int $int2")
    }

    @Test
    fun testShift() {
        var width = 100
        for (i in 0..10) {
            println("${width / 2} ${width shr 1}")
            width++
        }
    }

    @Test
    fun testGCode() {
        val gcode = "G21\n" +
                "G90 ;//\n" +
                "\n" +
                "G90\n" +
                ";注释;\n" +
                ";\n" +
                "\n" +
                "G1 F2000\n" +
                "M05 S0\n" +
                "G0 X-0.457921 Y4.150165\n" +
                "M03 S255\n" +
                "G1 X-0.457921 Y3.333333\n" +
                "G1 X-0.466172 Y3.325083\n" +
                "G1 X-3.758251 Y3.325083\n" +
                "G1 X-3.758251 Y2.450495"
        GCodeHelper.parseGCode(gcode)
    }

    @Test
    fun testGCode2() {
        val list = listOf(
            "M05 S0",
            "M05S0",
            "G1 F2000",
            "G1F2000",
            "G21 ;//xxx",
            "G21;//xxx",
            "G1X2 Y3 I04;//xxx",
            "G1X2Y3I04;//xxx",
            "G2X02Y03 I04;//xxx",
            "G2X02Y03I04;//xxx",
            "G1  X83.4949 Y-8.0145",
            "G1X83.4949Y-8.0145",
        )
        val regex = "[A-z][-]?[\\d.]*\\d+"
        for (gcode in list) {
            println(gcode.patternList(regex))
        }
    }

    @Test
    fun testFirmwareVersion() {
        val ex = ".lpbin"
        val pathList = listOf(
            "L2_N32_V3.5.7(1).lpbin",
            "L2_N32_V3.5.7_2022-7-8.lpbin",
            "/data/user/0/com.angcyo.uicore.demo/cache/documents/L2_N32_V3.5.7(1).lpbin"
        )
        pathList.forEach { path ->
            val r = path.substring(path.lastIndexOf("/") + 1, path.length - ex.length)

            val builder = StringBuilder()
            var isStart = false

            for (char in r.lowercase()) {
                if (isStart) {
                    if (char in '0'..'9') {
                        //如果是数字
                        builder.append(char)
                    } else if (char == '.') {
                        //继续
                    } else {
                        //中断
                        break
                    }
                } else {
                    if (char == 'v') {
                        isStart = true
                    }
                }
            }

            println(path.lastName())
            println(r)
            println(builder)
        }
    }

    @Test
    fun testVersion() {
        val version = 0
        println("$version".split("").connect("."))
    }

    @Test
    fun testForeach() {
        val list = mutableListOf(1, 2, 3, 4, 5)
        list.forEach {
            if (it in 3..3) {
                //只能 return 当前的action
                return@forEach
            }
            println(it)
        }
    }

    @Test
    fun testPick() {
        val text =
            "[120.211942, 30.175489],[120.21192, 30.173652],[120.214495, 30.173912],[120.214538, 30.17547],[120.211942, 30.175489],"
        val regex = "(?<=\\[)[^\\[\\]]+(?=\\])"

        regex.toRegex().findAll(text).forEachIndexed { index, matchResult ->
            println("$index -> ${matchResult.range} ${matchResult.value}")
        }

        var index = 0
        val matcher = regex.toPattern().matcher(text)
        while (matcher.find()) {
            print("${index++}->")
            println(matcher.group())
        }

        println("end...")
    }

    @Test
    fun testBinary() {
        val buildString = StringBuilder()
        buildString.append("01010101")
        println(buildString.toString().toByte(2))
    }

    /**3个点, 求圆心*/
    @Test
    fun testCenterOfCircle() {
        val p1x = 0f
        val p1y = 0f
        val p2x = 50f
        val p2y = -50f
        val p3x = 100f
        val p3y = 0f

        val tempA1: Float = p1x - p2x
        val tempA2: Float = p3x - p2x
        val tempB1: Float = p1y - p2y
        val tempB2: Float = p3y - p2y
        val tempC1: Float = ((p1x.toDouble().pow(2.0) - p2x.toDouble()
            .pow(2.0) + p1y.toDouble()
            .pow(2.0) - p2y.toDouble()
            .pow(2.0)) / 2).toFloat()
        val tempC2: Float = ((p3x.toDouble().pow(2.0) - p2x.toDouble()
            .pow(2.0) + p3y.toDouble()
            .pow(2.0) - p2y.toDouble().pow(2.0)) / 2).toFloat()
        val temp: Float = tempA1 * tempB2 - tempA2 * tempB1
        val x: Float
        val y: Float
        if (temp == 0f) {
            x = p1x
            y = p1y
        } else {
            x = (tempC1 * tempB2 - tempC2 * tempB1) / temp
            y = (tempA1 * tempC2 - tempA2 * tempC1) / temp
        }

        println("圆心: x:${x} y:${y}")
    }

    @Test
    fun testBit() {
        val value = 17
        println(value.toByte().high4Bit())
        println(value.toByte().low4Bit())
    }
}
