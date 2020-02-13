package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppNotifyItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/13
 */
class NotifyDemo : AppDslFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _adapter + AppNotifyItem()
    }
}