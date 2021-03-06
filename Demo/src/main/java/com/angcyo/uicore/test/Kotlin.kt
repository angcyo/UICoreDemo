package com.angcyo.uicore.test

import com.angcyo.library.L
import com.google.gson.Gson

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

fun test() {
    //testInt()
    //testJson()
}

fun testInt() {
    val colorArray = Array(4) { Array(4) { 0 } }

    for (i in colorArray.indices) { //根据下标遍历
        for (j in colorArray[i].indices) {

            val value1 = colorArray[i][j]
            val value = colorArray[i][j]

            L.e("TAG", "==11===,$value, $value1")
            // Log.e("TAG","==22==,$i, $j")
        }
    }
}

fun testJson() {
    val text = "{\"ABC\":\"\"}"
    val obj = Gson().fromJson(text, TestJson::class.java)
    L.i(obj)
}

class TestJson {
    var ABC: String? = null
}