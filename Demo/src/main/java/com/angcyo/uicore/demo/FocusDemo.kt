package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppFocusItem
import com.angcyo.widget.recycler.DslRecyclerView
import com.angcyo.widget.recycler.DslRecyclerView.Companion.addFocusTransitionChangeListener
import com.angcyo.widget.recycler.DslRecyclerView.Companion.removeFocusTransitionChangeListener
import com.angcyo.widget.recycler.FocusTransitionChanged
import com.angcyo.widget.recycler.initDslAdapter
import com.angcyo.widget.recycler.resetLayoutManager

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/05/14
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class FocusDemo : AppDslFragment() {

    val listener: FocusTransitionChanged = { from, to ->
        from?.apply {
            if (this !is RecyclerView) {
                this.animate().scaleX(1f).scaleY(1f).setDuration(300).start()
                this.isSelected = false
            }
        }
        to?.apply {
            if (this !is RecyclerView) {
                this.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start()
                this.isSelected = true
            }
        }
    }

    init {
        fragmentLayoutId = R.layout.fragment_focus
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            for (i in 0..100) {
                AppFocusItem()()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFocusTransitionChangeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeFocusTransitionChangeListener(listener)
    }

    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        recyclerView.resetLayoutManager("GV4")
        recyclerView.requestFocus()

        if (recyclerView is DslRecyclerView) {
            recyclerView.enableFocusTransition = true
        }

        _vh.rv(R.id.recycler_header_view)?.apply {
            if (this is DslRecyclerView) {
                enableFocusTransition = true
            }
            resetLayoutManager("GV4")
            initDslAdapter {
                for (i in 0..100) {
                    AppFocusItem()()
                }
            }
        }
    }
}