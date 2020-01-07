package com.angcyo.uicore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.base.getColor
import com.angcyo.core.component.toast
import com.angcyo.core.fragment.BaseDslFragment
import com.angcyo.coroutine.coroutineTest
import com.angcyo.coroutine.launch
import com.angcyo.drawable.dpi
import com.angcyo.drawable.toDpi
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.http.ApiKt
import com.angcyo.http.dslHttp
import com.angcyo.item.DslTextInfoItem
import com.angcyo.library.L
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.fragment.demo.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class MainFragment : BaseDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            renderMainItem("FragmentInFragmentDemo", 10.toDpi()) {
                dslAHelper {
                    start(FragmentInFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPagerInFragmentDemo") {
                dslAHelper {
                    start(ViewPagerInFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPagerInViewPagerDemo") {
                dslAHelper {
                    start(ViewPagerInViewPagerActivity::class.java)
                }
            }
            renderMainItem("ViewPager2InFragmentDemo") {
                dslAHelper {
                    start(ViewPager2InFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPager2InViewPager2Demo") {
                dslAHelper {
                    start(ViewPager2InViewPager2Activity::class.java)
                }
            }

            renderMainItem("WidgetDemo ArcLoadingView")
            renderMainItem("RefreshEffectDemo")
            renderMainItem("RefreshDemo")


//            for (i in 0..100) {
//                renderMainItem("ViewPager2InFragmentDemo") {
//                    dslAHelper {
//                        start(ViewPager2InFragmentActivity::class.java)
//                    }
//                }
//            }
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)
        coroutineTest()
        //rxJavaTest()

        launch {
            val response =
                dslHttp(ApiKt::class.java).post("https://www.mxnzp.com/api/tools/system/time")

            L.i(response)
        }

    }

    fun DslAdapter.renderMainItem(
        text: CharSequence?,
        topInsert: Int = 1 * dpi,
        fragment: Class<out Fragment>? = null,
        init: DslAdapterItem.() -> Unit = {},
        click: ((View) -> Unit)? = null
    ) {
        this + DslTextInfoItem().apply {
            itemInfoText = "${this@renderMainItem.adapterItems.size + 1}.$text"
            itemTopInsert = topInsert
            itemInfoIcon = R.drawable.ic_logo_small
            itemDarkIcon = R.drawable.lib_next
            itemDarkIconColor = getColor(R.color.colorPrimaryDark)

            onItemClick = { view ->

                var cls: Class<out Fragment>? = fragment
                val className = "com.angcyo.uicore.demo.${text?.split(" ")?.get(0)}"
                try {
                    if (fragment == null) {
                        cls = Class.forName(className) as? Class<out Fragment>
                    }
                } catch (e: Exception) {
                    //Tip.tip("未找到类:\n$className")
                    if (click == null) {
                        toast("未找到类:\n$className")
                    }
                }

                cls?.let {
                    dslFHelper {
                        show(it)
                    }
                }

                click?.invoke(view)
            }

            this.init()
        }
    }
}