package com.angcyo.uicore

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.base.getColor
import com.angcyo.behavior.HideTitleBarBehavior
import com.angcyo.coroutine.launch
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.toDpi
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.http.ApiKt
import com.angcyo.http.dslHttp
import com.angcyo.item.DslLastDeviceInfoItem
import com.angcyo.item.DslTextInfoItem
import com.angcyo.library.L
import com.angcyo.library.ex.getColor
import com.angcyo.library.toast
import com.angcyo.uicore.activity.*
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.R
import com.angcyo.widget.base.reveal
import com.angcyo.widget.recycler.allViewHolder
import com.angcyo.widget.recycler.get

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class MainFragment : AppDslFragment() {

    companion object {
        var CREATE_COUNT = 0
        const val GO = "√"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CREATE_COUNT++
    }

    /**锁定Demo的位置, 每次启动时自动跳转到这个Demo*/
    var lockDemoPosition: Int = RecyclerView.NO_POSITION

    override fun onInitFragment() {
        super.onInitFragment()
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

            renderMainItem("DrawableSpanDemo")
            renderMainItem("WidgetDemo ArcLoadingView")
            renderMainItem("RefreshEffectDemo")
            renderMainItem("RefreshDemo")
            renderMainItem("DslAffectDemo")
            renderMainItem("ValueTextWatcherDemo DslSoftInputLayout")
            renderMainItem("DslSoftInputDemo")
            renderMainItem("ViewModelDemo")
            renderMainItem("ViewGroupOverlayDemo")
            renderMainItem("TransitionDemo")
            renderMainItem("GlideImageDemo $GO")
            renderMainItem("RegularPatternDemo")
            renderMainItem("OkDownloadDemo")
            renderMainItem("MediaPickerDemo $GO")
            renderMainItem("DialogDemo")

//            for (i in 0..100) {
//                renderMainItem("ViewPager2InFragmentDemo") {
//                    dslAHelper {
//                        start(ViewPager2InFragmentActivity::class.java)
//                    }
//                }
//            }

            DslLastDeviceInfoItem()()
        }
    }

    override fun onCreateBehavior(child: View): CoordinatorLayout.Behavior<*>? {
        if (child.id == R.id.lib_title_wrap_layout) {
            return HideTitleBarBehavior(fContext()).apply {
                enableOverScroll = true
                enableEdgeScroll = enableOverScroll
            }
        }
        return super.onCreateBehavior(child)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)

        launch {
            val url = "https://www.mxnzp.com/api/tools/system/time"
            L.i("开始请求:")
            try {
                val response = dslHttp(ApiKt::class.java).post(url)
                L.i(response)
            } catch (e: Exception) {
                L.e("捕捉异常:$e")
                throw  e
            }
        }

        //揭露动画
        if (CREATE_COUNT % 2 != 0) {
            //偶数次
            _vh.itemView.reveal {
                animator?.duration = 700
            }
        }

        //自动跳转至指定Demo
        if (lockDemoPosition >= 0) {
            _vh.postDelay(300) {
                _adapter[lockDemoPosition]?.onItemClick?.invoke(_vh.itemView)
            }
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)
        //coroutineTest()
        //rxJavaTest()

        _vh.rv(R.id.lib_recycler_view)?.apply {
            allViewHolder().forEachIndexed { index, dslViewHolder ->
                //L.i("$index ${dslViewHolder.adapterPosition}")
            }

            L.i("first:${this[0]?.adapterPosition} last:${this[-1]?.adapterPosition}")
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

        if (text?.contains('√', true) == true) {
            lockDemoPosition = this.adapterItems.lastIndex
        }
    }
}