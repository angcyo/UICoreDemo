package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.behavior.refresh.IRefreshContentBehavior
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.SwipeMenuHelper
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.DslSwipeMenuItem
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/07
 */

class RefreshDemo : AppDslFragment() {

    init {
        enableRefresh = true
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        loadData()
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        startRefresh()
    }

    override fun onRefresh(refreshContentBehavior: IRefreshContentBehavior?) {
        L.i("收到刷新回调...${nowTime().fullTime()}")
        _vh.postDelay(2_000) {
            L.i("请求结束刷新!")
            loadData()
            finishRefresh()
        }
    }

    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        SwipeMenuHelper.install(recyclerView)
    }

    fun loadData() {
        renderDslAdapter {
            clearItems()
            for (i in 0..nextInt(0, 100)) {
                DslSwipeMenuItem()() {
                    itemInfoText = "Text...$i"
                    itemDarkText = nowTimeString()
                    itemTopInsert = 1 * dpi
                }
            }
            fragmentTitle = "${this@RefreshDemo.simpleClassName()}(${adapterItems.size})"
        }
    }
}