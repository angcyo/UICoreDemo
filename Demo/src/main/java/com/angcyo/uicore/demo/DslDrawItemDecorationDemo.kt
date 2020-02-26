package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.library.ex._drawable
import com.angcyo.uicore.dslitem.AppImageItem
import com.angcyo.widget.recycler.decoration.dslDrawItemDecoration
import com.angcyo.widget.recycler.decoration.itemPosition
import com.angcyo.widget.recycler.resetLayoutManager
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/26
 */

class DslDrawItemDecorationDemo : GlideImageDemo() {
    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        recyclerView.resetLayoutManager("GV5")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDslAdapter {
            clearItems()
            for (i in 0..10) {
                AppImageItem()() {
                    imageHeight = 0

                    onItemClick = {
                        pager()
                    }
                }
            }
        }

        dslDrawItemDecoration(_recycler) {
            onItemDrawOver = {
                val itemPosition = it.itemPosition()
                if (itemPosition == 3 || itemPosition == 8) {
                    drawItemDecoration(it) {
                        drawLayoutId = R.layout.layout_draw_item_decoration_1
                        onInitLayout = {
                            it.findViewById<TextView>(R.id.lib_text_view)?.text = span {
                                append("矫情的分割线") {
                                    foregroundColor = Color.WHITE
                                }
                                append("$itemPosition") {
                                    foregroundColor = Color.RED
                                }
                            }
                        }
                        if (itemPosition == 8) {
                            drawGravity = Gravity.TOP
                        }
                    }
                } else if (itemPosition == 5) {
                    drawItemDecoration(it) {
                        drawDrawable = _drawable(R.drawable.ic_logo)
                    }
                }
            }
        }
    }
}