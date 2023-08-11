package com.angcyo.uicore.base

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.base.dslFHelper
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.itemIndexPosition
import com.angcyo.dsladapter.updateItemSelected
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.style.itemInfoText
import com.angcyo.library.component.hawk.HawkPropertyValue
import com.angcyo.library.component.pad.isInPadMode
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.getColor
import com.angcyo.library.ex.isRtlContext
import com.angcyo.library.toast
import com.angcyo.uicore.demo.R

/**
 * 自动识别class, 跳转的demo, list item 界面
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/20
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseDemoDslFragment : AppDslFragment() {

    companion object {
        const val GO = "√"

        /**最后一次点击跳转的位置*/
        var lastJumpPosition: Int by HawkPropertyValue<Any, Int>(-1)
    }

    /**调转首次遇到的[GO], 否则则是最后一次*/
    var goFirst = false

    /**锁定Demo的位置, 每次启动时自动跳转到这个Demo*/
    var lockDemoPosition: Int = RecyclerView.NO_POSITION

    var baseClassPackage: String = "com.angcyo.uicore.demo"

    //第一个/最后一个包含Go的索引
    var _firstGoPosition: Int = RecyclerView.NO_POSITION
    var _lastGoPosition: Int = RecyclerView.NO_POSITION

    fun DslAdapter.renderDemoListItem(
        text: CharSequence?,
        topInsert: Int = 1 * dpi,
        fragment: Class<out Fragment>? = null,
        init: DslTextInfoItem.() -> Unit = {},
        click: ((View) -> Unit)? = null
    ) {
        this + DslTextInfoItem().apply {
            val last = if (adapterItems.size == lastJumpPosition) {
                "last"
            } else {
                ""
            }

            if (isRtlContext) {
                itemInfoText = "$last $text.${this@renderDemoListItem.adapterItems.size + 1}"
                itemDarkIcon = R.drawable.lib_prev
            } else {
                itemInfoText = "${this@renderDemoListItem.adapterItems.size + 1}.$text  $last"
                itemDarkIcon = R.drawable.lib_next
            }

            itemTopInsert = topInsert
            itemInfoIcon = R.drawable.ic_logo_small

            itemDarkIconColor = getColor(R.color.colorPrimaryDark)

            itemAnimateRes = R.anim.item_scale_animation
            itemSingleSelectMutex = true

            if (isInPadMode()) {
                itemBackgroundDrawable = _drawable(R.drawable.lib_bg_checked_selector)
            }

            itemClick = { view ->
                if (!itemIsSelected) {

                    var cls: Class<out Fragment>? = fragment
                    val className = "$baseClassPackage.${text?.split(" ")?.get(0)}"
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
                        /*dslAHelper {
                            start(it)
                        }*/
                        dslFHelper {
                            lastJumpPosition = itemIndexPosition()
                            if (isInPadMode()) {
                                configDetailContainer()
                                removeAll()
                            }
                            show(it)
                        }
                    }

                    click?.invoke(view)

                    //平板模式下, 选中
                    if (isInPadMode()) {
                        updateItemSelected()
                    }
                }
            }

            this.init()
        }

        //跳转索引支持
        if (text?.contains(GO, true) == true) {
            if (_firstGoPosition == RecyclerView.NO_POSITION) {
                _firstGoPosition = this.adapterItems.lastIndex
            }
            _lastGoPosition = this.adapterItems.lastIndex

            //
            lockDemoPosition = if (goFirst) {
                _firstGoPosition
            } else {
                _lastGoPosition
            }
        }
    }

}