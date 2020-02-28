package com.angcyo.uicore.demo

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.component.DslShortcut
import com.angcyo.library.component.dslShortcut
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.utils.ShortcutUtil
import com.angcyo.putData
import com.angcyo.uicore.activity.ShortcutActivity
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ShortcutDemo : AppDslFragment() {

    init {
        enableSoftInput = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            DslAdapterItem()() {
                itemLayoutId = R.layout.demo_shortcut

                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.click(R.id.button1) {
                        _shortcut(itemHolder)
                    }
                    itemHolder.click(R.id.button2) {
                        _shortcut(itemHolder) {
                            shortcutLabel = nowTimeString("HH:mm:ss")
                            shortcutIconId = R.drawable.ic_logo
                        }
                    }

                    itemHolder.click(R.id.button3) {
                        itemHolder.tv(R.id.text_view)?.text = span {
                            val title = itemHolder.tv(R.id.shortcut_label)?.string()
                            append("找到快捷方式名:$title")

                            val count = ShortcutUtil.getShortcutCount(
                                attachContext,
                                title
                            )
                            append(" $count")
                            append("个 ")
                            append(nowTimeString())
                        }
                    }

                    itemHolder.hawkInstallAndRestore("shortcut")
                }
            }
        }
    }

    fun _shortcut(itemHolder: DslViewHolder, action: DslShortcut.() -> Unit = {}) {

        fun action(id: Int): Int {
            return if (itemHolder.isChecked(id)) {
                itemHolder.view(id)?.tag?.toString()?.toIntOrNull() ?: 0
            } else {
                0
            }
        }

        dslShortcut(fContext()) {
            shortcutIntent = Intent().apply {
                component = ComponentName(fContext(), ShortcutActivity::class.java)
                putData(nowTimeString())
            }
            shortcutLabel = itemHolder.tv(R.id.shortcut_label)?.string()
            shortcutId = itemHolder.tv(R.id.shortcut_id)?.string()
            shortcutIconId = R.drawable.ic_logo_png

            shortcutAction = action(R.id.action_pin_shortcut) or
                    action(R.id.action_dynamic_shortcut) or
                    action(R.id.action_remove_shortcut) or
                    action(R.id.action_update_shortcut) or
                    action(R.id.action_remove_all_shortcut) or
                    action(R.id.action_install_shortcut)

            action()
        }
    }
}