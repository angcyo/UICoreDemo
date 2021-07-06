package com.angcyo.uicore.fragment

import android.os.Bundle
import com.angcyo.item.DslTextItem
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/07/06
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class ListFragment : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            for (i in 0..nextInt(40, 100)) {
                DslTextItem()() {
                    itemText = "$fragmentTitle ${nowTimeString()}"
                }
            }
        }
    }
}