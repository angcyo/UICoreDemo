package com.angcyo.uicore.demo

import android.content.Intent
import android.os.Bundle
import com.angcyo.library.L
import com.angcyo.picker.DslPicker
import com.angcyo.picker.dslPicker
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
                    dslPicker(it)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        L.i(DslPicker.onActivityResult(requestCode, resultCode, data))
    }
}