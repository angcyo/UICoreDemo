package com.angcyo.uicore.dslitem

import android.view.Gravity
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/29
 */
val textList = listOf(
    "矫情的分割线",
    "无中生有, 暗度陈仓"
)

fun tx(): String {
    return textList[nextInt(textList.size)]
}

fun gravity(): Int {
    val i = nextInt(2)
    val j = nextInt(2)
    val k = nextInt(4)

    var result = 0

    result = when (i) {
        1 -> result or Gravity.LEFT
        else -> result or Gravity.RIGHT
    }
    result = when (j) {
        1 -> result or Gravity.TOP
        else -> result or Gravity.BOTTOM
    }
    when (k) {
        1 -> result = result or Gravity.CENTER
        2 -> result = result or Gravity.CENTER_HORIZONTAL
        3 -> result = result or Gravity.CENTER_VERTICAL
    }
    return result
}