package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppFlatImageItem
import com.angcyo.uicore.dslitem.AppImageItem
import com.angcyo.widget.recycler.resetLayoutManager

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/20
 */

class GlideImageDemo : AppDslFragment() {

    override fun onInitDslLayout() {
        super.onInitDslLayout()

        _vh.rv(R.id.lib_recycler_view)?.resetLayoutManager("SV2")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        renderDslAdapter {
            for (i in 0..100) {
                AppImageItem(i)()
            }

            for (i in 0..2) {
                AppFlatImageItem(i)()
            }
        }
    }
}