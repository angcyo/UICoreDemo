package com.angcyo.uicore.demo

import com.angcyo.bluetooth.fsc.laserpacker.command.QueryCmd
import com.angcyo.gcode.GCodeHelper
import com.angcyo.laserpacker.parseGCode
import com.angcyo.library.ex.connect
import com.angcyo.library.ex.high4Bit
import com.angcyo.library.ex.lastName
import com.angcyo.library.ex.low4Bit
import com.angcyo.library.ex.nowTime
import com.angcyo.library.ex.padHexString
import com.angcyo.library.ex.patternList
import com.angcyo.library.ex.size
import com.angcyo.library.ex.toAsciiInt
import com.angcyo.library.ex.toAsciiString
import com.angcyo.library.ex.toHexByteArray
import com.angcyo.library.ex.toHexInt
import com.angcyo.library.ex.toHexString
import com.angcyo.library.ex.uuid
import com.angcyo.library.utils.CRC16.crc16Hex
import com.angcyo.uicore.test.PathTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


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
        println(QueryCmd("", 0).toHexCommandString())
        println(QueryCmd("", 1).toHexCommandString())
        println(QueryCmd("", 2).toHexCommandString())
        println(QueryCmd("", 3).toHexCommandString())
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
        val value = 421
        println(value.toByte().high4Bit())
        println(value.toByte().low4Bit())

        //高8位
        val h = (value shr 8).toByte()
        //低8位
        val l = (value and 0xFF).toByte()
        println("h:${h} l:${l}")
    }

    @Test
    fun testRandom() {
        val result = mutableListOf<Int>()
        //1682135311648 269520350402700 -1623236339
        println("${System.currentTimeMillis()} ${System.nanoTime()} ${uuid().hashCode()}")

        /*for (i in 0..999999) {
            //val index = System.nanoTime().toInt() //第:428491 次后出现重复
            val index = (System.nanoTime() shr 4).toInt().absoluteValue //第:633577 次后出现重复
            //重复:-724726203 第:658114 次后出现重复
            //重复:14527200 第:309596 次后出现重复
            if (result.contains(index)) {
                println("重复:$index 第:$i")
            } else {
                result.add(index)
            }
        }*/

        /*for (i in 0..999999) {
            //val index = EngraveHelper.generateEngraveIndex()
            val index = uuid().hashCode() //第:79009 次后出现重复
            if (result.contains(index)) {
                println("重复:$index 第:$i")
            } else {
                result.add(index)
            }
        }*/

        for (i in 0..999999) {
            val index = System.currentTimeMillis().toInt().absoluteValue
            if (result.contains(index)) {
                println("重复:$index 第:$i")
            } else {
                result.add(index)
            }
            Thread.sleep(1)
        }
    }

    @Test
    fun testBits() {
        /*val num1 = 100
        val num2 = 200

        val num = num1 or (num2 shl 8)

        val num1_1 = num and 0xFF
        val num2_2 = (num shr 8) and 0xFF

        println("num1:$num1 $num1_1")
        println("num2:$num2 $num2_2")*/

        val time = nowTime()
        val num = Int.MAX_VALUE

        val sum = time shl 32 or num.toLong()

        println("time:$time ${sum shr 32}")
        println("num:$num ${sum and 0xFFFFFFFF}")
    }

    @Test
    fun testSvg() {
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
        println(pathData)
    }

    @Test
    fun testIncrease() {
        val fromIndex = 41
        val toIndex = 60

        val fromValue = 20f
        val valueStep = -0.5f

        val list = listOf(fromIndex, fromIndex + 11, toIndex)
        for (index in list) {
            val value = fromValue + (index - fromIndex) * valueStep
            println("$index -> $value")
        }
    }

    /**
     * [com.angcyo.bluetooth.fsc.laserpacker.bean.DeviceConfigBeanKt.toSliceLevelList]
     * */
    @Test
    fun testSlice() {
        val max = 254
        val count = 10
        val step = maxOf(1, max / count)

        val list = mutableListOf<Int>()
        while (list.size() < count) {
            val value = maxOf(0, max - step * list.size())
            if (value == list.lastOrNull()) {
                break
            }
            list.add(value)
        }
        /*for (i in max downTo 1 step max / maxOf((count - 1), 1)) {
            list.add(i)
        }*/
        print(list.size())
        println(list)
    }

    /**
     * [com.angcyo.engrave2.transition.EngraveTransitionHelper.getSliceThresholdList]
     * */
    @Test
    fun testSliceList() {
        //图片中存在的色阶值
        val colors = listOf(250, 245, 235, 0)
        //最黑色颜色
        val minColor = colors.minOrNull() ?: 0
        //最白色颜色
        val maxColor = colors.maxOrNull() ?: 255

        //需要切片的数量
        val sliceCount = 255 - minColor

        //预估每一片的理论色阶值
        val sliceList = mutableListOf<Int>()
        if (sliceCount <= 1) {
            sliceList.add(maxColor)
        } else {
            for (i in 0 until sliceCount) {
                val value =
                    maxColor - ((maxColor - minColor) * 1f / (sliceCount - 1) * i).roundToInt()
                sliceList.add(value)
            }
        }
        sliceList.sortDescending() //从大到小排序
        println(sliceList)

        val list = mutableListOf<Int>()
        for (i in 0 until sliceCount) {
            val ref = sliceList[i]
            val value = colors.minByOrNull {
                if (it <= ref) {
                    (it - ref).absoluteValue
                } else {
                    Int.MAX_VALUE
                }
            } ?: 0
            list.add(value)
        }

        println(colors)
        print("->${list.size()}")
        println(list)
    }

    @Test
    fun testCRC16() {
        val data1 = MutableList(100, init = { it.toByte() })
        val data2 = MutableList(100, init = { (it % 10).toByte() })
        val crc1 = data1.toByteArray().crc16Hex()
        val crc2 = data2.toByteArray().crc16Hex()
        println(data1)
        println(crc1)
        println(data2)
        println(crc2)
    }

    @Test
    fun testAscii() {
        val int = 2116779516
        //将二进制int转成ascii码对应的字符串
        val ascii = int.toAsciiString()
        println(ascii)
        //反解ascii成int
        println(ascii.toAsciiInt())
        println("7abc483c".toAsciiInt())
    }

    @Test
    fun testSum() {
        //给定N个数
        val numList = listOf(8, 16, 23, 32, 40, 48)
        var target = 24
        //给定目标值
        //求出使用最少数相加等于目标值的算法
        val resultList = mutableListOf<Int>()

        while (target > 0) {
            val value = findMaxNumIn(numList, target)
            if (value != null) {
                resultList.add(value)
                target -= value
            } else {
                println(target)
                break
            }
        }

        println(resultList)
    }

    /**查找最大的小于[sum]的数*/
    fun findMaxNumIn(list: List<Int>, sum: Int): Int? {
        var result: Int? = null
        var dx = Int.MAX_VALUE

        for (num in list) {
            if (num <= sum) {
                val d = sum - num
                if (d < dx) {
                    dx = d
                    result = num
                }
            }
        }

        return result
    }

    @Test
    fun testSum2() {
        //N数求和/最接近目标值的组合
        val numList = listOf(8, 16, 23, 32, 40, 48)
        val target = 24

        println(findCombinationNumbers(target, numList))
    }

    fun findCombinationNumbers(target: Int, numList: List<Int>): List<Int> {
        val resultList = mutableListOf<Int>()
        val remainder = findCombinationNumbers(target, numList, resultList)
        if (remainder > 0 && remainder < numList.min()) {
            println(remainder)
            //还有余数
            return findCombinationNumbers(target + 1, numList)
        }
        return resultList
    }

    fun findCombinationNumbers(target: Int, numList: List<Int>, resultList: MutableList<Int>): Int {
        val array = IntArray(target + 1)
        array.fill(Int.MAX_VALUE)
        for (num in numList) {
            for (i in num..target) {
                array[i] = min(array[i], array[i - num] + 1)
            }
        }
        var currentSum = target
        for (num in numList) {
            while (currentSum >= num && array[currentSum] == array[currentSum - num] + 1) {
                resultList.add(num)
                currentSum -= num
            }
        }
        return currentSum
    }
}
