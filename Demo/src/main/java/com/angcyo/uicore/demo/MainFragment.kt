package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslChildFHelper
import com.angcyo.core.component.file.DslFileHelper
import com.angcyo.core.fragment.BaseDslFragment
import com.angcyo.core.item.DslTextInfoItem
import com.angcyo.coroutine.coroutineTest
import com.angcyo.drawable.dpi
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.http.rx.rxJavaTest
import com.angcyo.uicore.demo.fragment.demo.FragmentInFragmentActivity
import com.angcyo.uicore.demo.fragment.demo.ViewPager2InFragmentActivity
import com.angcyo.uicore.demo.fragment.demo.ViewPagerInFragmentActivity

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
            renderMainItem("FragmentInFragmentDemo") {
                dslAHelper {
                    start(FragmentInFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPagerInFragmentDemo") {
                dslAHelper {
                    start(ViewPagerInFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPager2InFragmentDemo") {
                dslAHelper {
                    start(ViewPager2InFragmentActivity::class.java)
                }
            }
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)
        coroutineTest()
        rxJavaTest()

        DslFileHelper.test()
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

            onItemClick = { view ->

                var cls: Class<out Fragment>? = fragment
                val className = "com.angcyo.uicore.demo.${text?.split(" ")?.get(0)}"
                try {
                    if (fragment == null) {
                        cls = Class.forName(className) as? Class<out Fragment>
                    }
                } catch (e: Exception) {
                    //Tip.tip("未找到类:\n$className")
                }

                cls?.let {
                    dslChildFHelper {
                        show(it)
                    }
                }

                click?.invoke(view)
            }

            this.init()
        }
    }
}