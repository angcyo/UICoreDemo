package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.View
import com.angcyo.dsladapter.DslAdapterStatusItem
import com.angcyo.library.component._delay
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppJsoupHtmlItem
import com.angcyo.uicore.dslitem.AppJsoupInputItem
import com.angcyo.widget.base.onDoubleTap

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/14
 */
class JsoupDemo : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _adapter.setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_LOADING)

        _delay {
            renderDslAdapter {
                AppJsoupInputItem()()
                AppJsoupHtmlItem()()

                _adapter.setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_NONE)
            }
        }

        titleControl()?.selectorView?.onDoubleTap {
            _recycler.lockScroll(0, 16)
            true
        }
    }
}
