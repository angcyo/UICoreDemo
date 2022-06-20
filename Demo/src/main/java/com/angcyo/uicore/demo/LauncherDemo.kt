package com.angcyo.uicore.demo

import android.content.Intent
import android.os.Bundle
import android.os.Trace
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.coroutine.onBack
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.loadingStatus
import com.angcyo.dsladapter.margin
import com.angcyo.library.L
import com.angcyo.library.LTime
import com.angcyo.library.component.appBean
import com.angcyo.library.component.dslIntentQuery
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppItem
import com.angcyo.widget.recycler.resetLayoutManager

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class LauncherDemo : AppDslFragment() {

    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        recyclerView.resetLayoutManager("GV4")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adapter.render {
            loadingStatus()
        }
        launchLifecycle {
            LTime.tick()
            Trace.beginSection("query")
            onBack {
                dslIntentQuery {
                    queryAction = Intent.ACTION_MAIN
                    queryCategory = listOf(Intent.CATEGORY_LAUNCHER)
                }.apply {
                    renderDslAdapter {
                        forEachIndexed { index, resolveInfo ->
                            AppItem()() {
                                margin(1 * dpi)
                                appBean = resolveInfo.activityInfo?.packageName?.appBean()
                            }
                        }
                    }
                }
            }.await().run {
                Trace.endSection()
                L.i("总耗时:${LTime.time()}")
                fragmentTitle = "$fragmentTitle $size"
            }
        }
    }
}