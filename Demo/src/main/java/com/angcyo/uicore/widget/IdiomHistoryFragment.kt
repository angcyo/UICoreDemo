package com.angcyo.uicore.widget

import android.os.Bundle
import com.angcyo.core.fragment.BaseDslFragment

/**
 * 成语历史记录
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/06/13
 */
class IdiomHistoryFragment : BaseDslFragment() {

    init {
        fragmentTitle = "学习历史"
        page.singlePage()
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        IdiomHelper.init()
        loadDataEnd(IdiomHistoryItem::class, IdiomHelper.allIdiomList)
    }

}