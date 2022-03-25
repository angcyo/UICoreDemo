package com.angcyo.uicore.demo

import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.library.ex.padHexString
import com.angcyo.library.ex.toHexByteArray
import com.angcyo.library.ex.toHexInt
import com.angcyo.library.ex.toHexString
import org.junit.Assert.assertEquals
import org.junit.Test

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
        println(LaserPeckerHelper.stateCmd(0))
        println(LaserPeckerHelper.stateCmd(1))
        println(LaserPeckerHelper.stateCmd(2))
        println(LaserPeckerHelper.stateCmd(3))
        println("AA".padHexString(4))
        println("AA".padHexString(4, false))
        println("AA".toInt(16))
        println("0101".toInt(16))
        println("0000F924".toInt(16))
        val byte: Byte = -86
        val bytes = byteArrayOf()
        //val bytes = byteArrayOf(-86, -69)
        println("${byte.toHexInt()} ${byte.toHexString().toInt(16)}")
        println("${bytes.toHexInt()}")
    }
}
