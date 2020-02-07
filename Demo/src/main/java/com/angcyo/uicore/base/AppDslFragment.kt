package com.angcyo.uicore.base

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.behavior.HideTitleBarBehavior
import com.angcyo.core.R
import com.angcyo.core.fragment.BaseDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/10
 */
open class AppDslFragment : BaseDslFragment() {
    override fun onCreateBehavior(child: View): CoordinatorLayout.Behavior<*>? {
        return if (child.id == R.id.lib_title_wrap_layout) {
            HideTitleBarBehavior(fContext())
        } else {
            super.onCreateBehavior(child)
        }
    }
}