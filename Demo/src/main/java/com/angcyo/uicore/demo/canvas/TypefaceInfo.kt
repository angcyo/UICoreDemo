package com.angcyo.uicore.demo.canvas

import android.graphics.Typeface

/**
 * 字体信息
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/06/07
 */
data class TypefaceInfo(
    //字体显示的名字
    val name: String,
    //字体
    val typeface: Typeface
)
