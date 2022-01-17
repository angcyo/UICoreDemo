package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.component.StateModel
import com.angcyo.core.vmApp
import com.angcyo.dialog.normalIosDialog
import com.angcyo.dsladapter.dslItem
import com.angcyo.library.ex.nowTime
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment

/**
 * 组件测试demo
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/01/17
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class ComponentDemo : AppDslFragment() {

    init {

    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val state = "test"

        renderDslAdapter {
            dslItem(R.layout.component_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->

                    itemHolder.click(R.id.wait_forever_state_button) {
                        vmApp<StateModel>().waitState(state, true) { data, throwable ->
                            toastQQ("状态改变:$data")
                        }
                    }

                    itemHolder.click(R.id.wait_state_button) {
                        vmApp<StateModel>().waitState(state) { data, throwable ->
                            fContext().normalIosDialog {
                                dialogMessage = "状态改变:$data"
                            }
                        }
                    }

                    itemHolder.click(R.id.change_state_button) {
                        vmApp<StateModel>().updateState(state, nowTime())
                    }

                    itemHolder.click(R.id.wait_state_value_button) {
                        vmApp<StateModel>().waitState(state, 9999) { data, throwable ->
                            fContext().normalIosDialog {
                                dialogMessage = "状态改变:$data"
                            }
                        }
                    }

                    itemHolder.click(R.id.change_state_value_button) {
                        vmApp<StateModel>().updateState(state, 9999)
                    }
                }
            }
        }
    }

}