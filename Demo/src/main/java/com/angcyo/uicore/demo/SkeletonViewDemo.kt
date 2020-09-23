package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.drawable.skeleton.SkeletonDrawable
import com.angcyo.dsladapter.dslItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.SkeletonView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/09/22
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class SkeletonViewDemo : AppDslFragment() {

    companion object {
        var count = 0
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            dslItem(R.layout.demo_skeleton_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.v<SkeletonView>(R.id.skeleton_view)
                        ?.firstDrawable<SkeletonDrawable>()?.apply {
                            if ((count++ % 2) == 0) {
                                loadNormalSkeleton()
                            } else {
                                loadNormalSkeleton2()
                            }
                        }
                }
            }
        }
    }
}