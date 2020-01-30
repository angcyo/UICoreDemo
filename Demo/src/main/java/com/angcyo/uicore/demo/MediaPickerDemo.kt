package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.base.dslAHelper
import com.angcyo.picker.PickerActivity
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppMediaPickerItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/29
 */

class MediaPickerDemo : AppDslFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            AppMediaPickerItem()() {
                onStartPicker = {
                    dslAHelper {
                        start(PickerActivity::class.java)
                    }
                }
            }
        }
    }
}