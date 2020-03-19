package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.coroutine.onBack
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.library.ex._drawable
import com.angcyo.library.utils.gravityString
import com.angcyo.uicore.dslitem.*
import com.angcyo.widget.recycler.decoration.dslDrawItemDecoration
import com.angcyo.widget.recycler.decoration.itemPosition
import com.angcyo.widget.recycler.resetLayoutManager
import com.angcyo.widget.span.span
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/26
 */

class DslDrawItemDecorationDemo : GlideImageDemo() {

    val drawItemDecorationList = mutableListOf<DrawItemConfig>()

    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        recyclerView.resetLayoutManager("GV5")
    }

    fun initDrawItemDecorationPosition(columns: Int = 5, rows: Int = 30) {
        for (i in 0 until rows) {
            val position = i * columns + nextInt(columns)
            drawItemDecorationList.add(DrawItemConfig(position, tx(), gravity()))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDrawItemDecorationPosition()
        renderDslAdapter {
            clearItems()

            launchLifecycle {
                onBack {
                    for (i in 0..24) {
                        AppResImageItem()() {
                            itemImageRes = res()
                        }
                    }

                    for (i in 0 until 20) {
                        AppImageItem()() {
                            itemClick = {
                                pager()
                            }
                        }
                    }
                }
            }
            //updateItemDepend(FilterParams(just = true, async = false))
        }

        dslDrawItemDecoration(_recycler) {
            onItemDrawOver = {
                //L.w("this...")
                val itemPosition = it.itemPosition()
                drawItemDecorationList.find { it.position == itemPosition }?.run {
                    drawItemDecoration(it) {
                        drawLayoutId = R.layout.layout_draw_item_decoration_1
                        onInitLayout = {
                            it.findViewById<TextView>(R.id.lib_text_view)?.text = span {
                                append(text)
                                appendln()
                                append(gravity.gravityString())
                                append(" $itemPosition") {
                                    foregroundColor = Color.RED
                                }
                            }
                        }
                        drawGravity = gravity
                    }
                }

                if (itemPosition == 5) {
                    drawItemDecoration(it) {
                        drawDrawable = _drawable(R.drawable.ic_logo)
                    }
                }
            }
        }
    }
}

data class DrawItemConfig(var position: Int, var text: String?, var gravity: Int)