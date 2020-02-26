package com.angcyo.uicore.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.angcyo.core.activity.FragmentWrapActivity
import com.angcyo.dsladapter.marginVertical
import com.angcyo.item.DslTextInfoItem
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.nowTimeString
import com.angcyo.putData
import com.angcyo.uicore.MainFragment
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ShortcutFragment : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderDslAdapter {
            DslTextInfoItem()() {
                itemInfoText = "快捷方式测试\n$arguments"
                itemDarkText = nowTimeString()
            }

            DslTextInfoItem()() {
                marginVertical(2 * dpi)
                itemInfoText = "FragmentWrapActivity测试"
                itemDarkText = nowTimeString()

                onItemClick = {
                    FragmentWrapActivity.jump(
                        fContext(),
                        Intent(fContext(), ShortcutFragment::class.java).apply {
                            putData(nowTimeString())
                        },
                        false
                    )
                }
            }

            DslTextInfoItem()() {
                marginVertical(2 * dpi)

                itemInfoText = "FragmentWrapActivity测试(singleTask)"
                itemDarkText = nowTimeString()

                onItemClick = {
                    FragmentWrapActivity.jump(
                        fContext(),
                        Intent(fContext(), ShortcutFragment::class.java).apply {
                            putData(nowTimeString())
                        },
                        true
                    )
                }
            }

            DslTextInfoItem()() {
                marginVertical(2 * dpi)
                itemInfoText = "FragmentWrapActivity测试2"
                itemDarkText = nowTimeString()

                onItemClick = {
                    FragmentWrapActivity.jump(
                        fContext(),
                        Intent(fContext(), MainFragment::class.java).apply {
                            putData(nowTimeString())
                        },
                        false
                    )
                }
            }

            DslTextInfoItem()() {
                marginVertical(2 * dpi)

                itemInfoText = "FragmentWrapActivity测试2(singleTask)"
                itemDarkText = nowTimeString()

                onItemClick = {
                    FragmentWrapActivity.jump(
                        fContext(),
                        Intent(fContext(), MainFragment::class.java).apply {
                            putData(nowTimeString())
                        },
                        true
                    )
                }
            }
        }
    }
}