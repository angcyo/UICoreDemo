package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.behavior.refresh.RefreshBehavior
import com.angcyo.core.fragment.BaseDslFragment
import com.angcyo.drawable.dpi
import com.angcyo.item.DslBaseInfoItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/07
 */

class RefreshDemo : BaseDslFragment() {

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

    override fun onRefresh(refreshBehavior: RefreshBehavior?) {
        _vh.postDelay(2_000) {
            finishRefresh()
        }
    }
}