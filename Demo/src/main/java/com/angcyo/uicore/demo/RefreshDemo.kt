package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.behavior.BaseScrollBehavior
import com.angcyo.behavior.refresh.RefreshContentBehavior
import com.angcyo.library.ex.dpi
import com.angcyo.item.DslBaseInfoItem
import com.angcyo.library.L
import com.angcyo.library.ex.fullTime
import com.angcyo.library.ex.nowTime
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/07
 */

class RefreshDemo : AppDslFragment() {

    init {
        enableRefresh = true
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            for (i in 0..100) {
                DslBaseInfoItem()() {
                    itemInfoText = "Text...$i"
                    itemTopInsert = 1 * dpi
                }
            }
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        startRefresh()
    }

    override fun onRefresh(refreshContentBehavior: BaseScrollBehavior<*>?) {
        L.i("收到刷新回调...${nowTime().fullTime()}")
        _vh.postDelay(2_000) {
            L.i("请求结束刷新!")
            finishRefresh()
        }
    }
}