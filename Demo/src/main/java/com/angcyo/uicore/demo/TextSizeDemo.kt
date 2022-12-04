package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.dslitem.AppTextSizeDemoItem
import com.angcyo.widget.base.string

/**
 * 字体大小计算
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/12/04
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class TextSizeDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.demo_text_size_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                
                //--

                itemHolder.click(R.id.add_button) {
                    render {
                        AppTextSizeDemoItem()() {
                            itemDemoText = itemHolder.tv(R.id.lib_edit_view).string()
                            itemDemoTextSize =
                                itemHolder.tv(R.id.size_edit_view).string().toFloatOrNull() ?: 9f
                        }
                    }
                }

                //--

                itemHolder.click(R.id.add_12_button) {
                    render {
                        AppTextSizeDemoItem()() {
                            itemDemoText = itemHolder.tv(R.id.lib_edit_view).string()
                            itemDemoTextSize = 12f
                        }
                    }
                }

                itemHolder.click(R.id.add_24_button) {
                    render {
                        AppTextSizeDemoItem()() {
                            itemDemoText = itemHolder.tv(R.id.lib_edit_view).string()
                            itemDemoTextSize = 24f
                        }
                    }
                }

                itemHolder.click(R.id.add_30_button) {
                    render {
                        AppTextSizeDemoItem()() {
                            itemDemoText = itemHolder.tv(R.id.lib_edit_view).string()
                            itemDemoTextSize = 30f
                        }
                    }
                }
            }
        }
    }

}