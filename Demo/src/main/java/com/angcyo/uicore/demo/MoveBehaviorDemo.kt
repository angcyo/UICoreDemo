package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.core.math.MathUtils
import com.angcyo.behavior.BaseScrollBehavior
import com.angcyo.behavior.IScrollBehaviorListener
import com.angcyo.behavior.effect.MoveBehavior
import com.angcyo.library.L
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.base.mH
import com.angcyo.widget.base.setBehavior

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/10
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class MoveBehaviorDemo : AppTitleFragment() {
    init {
        fragmentLayoutId = R.layout.fragment_move_behavior
    }

    override fun onInitFragment(savedInstanceState: Bundle?) {
        super.onInitFragment(savedInstanceState)

        _vh.view(R.id.lib_content_wrap_layout)?.setBehavior(MoveBehavior(fContext()).apply {
            defaultOffsetY = "50dp"

            moveScrollY = {
                val min = 0
                val max = childView.mH() - 50 * dpi
                val targetY = MathUtils.clamp(it, min, max)
                targetY
            }

            moveBehaviorReset = { moveBehavior, behaviorScrollY ->
                val max = childView.mH() - 50 * dpi
                if (behaviorScrollY > 20 * dpi) {
                    moveBehavior.resetScrollTo(max)
                } else {
                    moveBehavior.resetScrollTo(0)
                }
            }

            addScrollListener(object : IScrollBehaviorListener {
                override fun onBehaviorScrollTo(
                    scrollBehavior: BaseScrollBehavior<*>,
                    x: Int,
                    y: Int,
                    scrollType: Int
                ) {
                    L.e(x, " ", y)
                    _vh.tv(R.id.lib_tip_text_view)?.text = "x:$x y:$y"
                }
            })
        })
    }
}