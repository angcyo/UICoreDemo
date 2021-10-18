package com.angcyo.uicore.dslitem

import android.view.Gravity
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.style.itemInfoText
import com.angcyo.library.ex.nowTimeString
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/29
 */
val textList = listOf(
    "矫情的分割线",
    "暗度陈仓",
    "金蝉脱壳",
    "抛砖引玉",
    "借刀杀人",
    "以逸待劳",
    "擒贼擒王",
    "趁火打劫",
    "关门捉贼",
    "浑水摸鱼",
    "打草惊蛇",
    "瞒天过海",
    "反间计",
    "笑里藏刀",
    "顺手牵羊",
    "调虎离山",
    "李代桃僵",
    "指桑骂槐",
    "隔岸观火",
    "树上开花",
    "暗渡陈仓",
    "走为上",
    "假痴不癫",
    "欲擒故纵",
    "釜底抽薪",
    "空城计",
    "苦肉计",
    "远交近攻",
    "反客为主",
    "上屋抽梯",
    "偷梁换柱",
    "无中生有",
    "美人计",
    "借尸还魂",
    "声东击西",
    "围魏救赵",
    "连环计",
    "假道伐虢"
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

/**快速加载demo数据*/
fun DslAdapter.loadTextItem(count: Int = nextInt(2, 40), action: DslTextInfoItem.() -> Unit = {}) {
    for (i in 0 until count) {
        DslTextInfoItem()() {
            itemInfoText = "Position:$i ${tx()}"
            itemDarkText = nowTimeString()
            action()
        }
    }
}