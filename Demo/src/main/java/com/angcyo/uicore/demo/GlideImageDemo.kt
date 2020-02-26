package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.pager.dslPager
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

open class GlideImageDemo : AppDslFragment() {

    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        recyclerView.resetLayoutManager("SV2")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDslAdapter {
            for (i in 0..100) {
                AppImageItem(i)() {
                    imageMask = true
                    onItemClick = {
                        pager()
//                        dslPager {
//                            fromView = it
//                            addMedia(imageUrl)
//                        }
                    }
                }
            }

            for (i in 0..2) {
                AppFlatImageItem(i)() {
                    onItemClick = {
                        pager()
                    }
                }
            }
        }
    }

    /**启动大图浏览*/
    fun AppImageItem.pager() {
        val item = this
        dslPager {
            transitionShowDelay = 30
            fromRecyclerView = _vh.rv(R.id.lib_recycler_view)
            _adapter.getValidFilterDataList().forEachIndexed { index, dslAdapterItem ->
                if (dslAdapterItem is AppImageItem) {
                    if (item == dslAdapterItem) {
                        startPosition = index
                    }
                    addMedia(dslAdapterItem.imageUrl)
                }
            }
        }
    }
}