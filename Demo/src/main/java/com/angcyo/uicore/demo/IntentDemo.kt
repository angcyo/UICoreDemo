package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.angcyo.base.dslAHelper
import com.angcyo.coroutine.onBack
import com.angcyo.dsladapter.renderItem
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.style.itemInfoText
import com.angcyo.library.component.appBean
import com.angcyo.library.component.dslIntentQuery
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class IntentDemo : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDslAdapter {
            launchLifecycle {
                onBack {
                    dslIntentQuery {

                    }.apply {
                        forEachIndexed { index, resolveInfo ->
                            DslTextInfoItem()() {
                                itemTopInsert = 2 * dpi
                                itemInfoText = span {
                                    append(resolveInfo.toString())
                                    resolveInfo.activityInfo?.run {
                                        packageName?.appBean()?.run {
                                            appendln()
                                            append("->")
                                            append(appName)
                                            append(" ")
                                            append(versionName)
                                            append(" ")
                                            append("$versionCode")
                                            append(" ")
                                        }
                                        targetActivity?.run {
                                            appendln()
                                            append(this)
                                        }
                                    }
                                }

                                itemDarkText = span {
                                    append("$index") {
                                        foregroundColor = Color.RED
                                    }
                                }

                                itemClick = {
                                    dslAHelper {
                                        start(resolveInfo.activityInfo?.packageName)
                                    }
                                }
                            }
                        }
                        renderItem {
                            itemLayoutId = R.layout.demo_intent
                        }
                    }
                }.await().apply {
                    fragmentTitle = "$fragmentTitle $size"
                }
            }
        }
    }
}