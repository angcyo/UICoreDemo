package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.fragment.BaseTitleFragment
import com.angcyo.widget.progress.ArcLoadingView
import com.angcyo.widget.progress.DslSeekBar

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class WidgetDemo : BaseTitleFragment() {
    override fun getContentLayoutId(): Int = R.layout.demo_widget

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        baseViewHolder.v<DslSeekBar>(R.id.seek_bar)?.config {
            onSeekChanged = { value, _, _ ->
                baseViewHolder.v<ArcLoadingView>(R.id.arc_load_view)?.run {
                    endLoading()
                    progress = value
                }
            }
        }

        baseViewHolder.click(R.id.button) {
            baseViewHolder.v<ArcLoadingView>(R.id.arc_load_view)?.startLoading()
        }

        //baseViewHolder.group(R.id.wrap_layout).helper()
    }
}