package com.angcyo.uicore.demo

import android.graphics.ColorFilter
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.angcyo.base.dslChildFHelper
import com.angcyo.core.fragment.BaseFragment
import com.angcyo.library.L
import com.angcyo.tablayout.TabGradientCallback
import com.angcyo.uicore.fragment.HomeFragment
import com.angcyo.uicore.fragment.ListFragment
import com.angcyo.uicore.fragment.MeFragment
import com.angcyo.widget.base.eachChild
import com.angcyo.widget.tab

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/07/06
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class NavigationDemo : BaseFragment() {
    init {
        fragmentLayoutId = R.layout.fragment_navigation
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val fragmentList =
            listOf(HomeFragment::class.java, ListFragment::class.java, MeFragment::class.java)

        _vh.tab(R.id.tab_layout)?.apply {

            eachChild { index, child ->

                //动态设置资源, 在xml中会有回调失败的问题, 导致界面不显示图标
                child.findViewById<LottieAnimationView>(R.id.lottie_view)?.setAnimation(
                    when (index) {
                        1 -> "json_add.json"
                        2 -> "json_play.json"
                        else -> "json_home.json"
                    }
                )
            }

            configTabLayoutConfig {

                //颜色渐变
                tabGradientCallback = object : TabGradientCallback() {
                    override fun onUpdateIcoColor(view: View?, color: Int) {
                        (view as? LottieAnimationView)?.apply {
                            setLottieColorFilter(color)
                        } ?: super.onUpdateIcoColor(view, color)
                    }
                }

                //选中view的回调
                onSelectViewChange = { fromView, selectViewList, reselect, fromUser ->
                    val toView = selectViewList.first()
                    fromView?.apply { onGetTextStyleView(this, -1)?.visibility = View.GONE }
                    if (reselect) {
                        //重复选择
                    } else {
                        toView.findViewById<LottieAnimationView>(R.id.lottie_view)?.playAnimation()
                        onGetTextStyleView(toView, -1)?.visibility = View.VISIBLE
                    }
                }

                //选中index的回调
                onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                    val toIndex = selectIndexList.first()
                    L.i("TabLayout选中改变:[$fromIndex]->[$toIndex]")

                    dslChildFHelper {
                        noAnim()
                        restore(fragmentList[toIndex])
                    }
                }
            }
        }
    }
}

fun LottieAnimationView.setLottieColorFilter(color: Int) {
    val filter = SimpleColorFilter(color)
    val keyPath = KeyPath("**")
    val callback = LottieValueCallback<ColorFilter>(filter)
    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}