package com.angcyo.uicore.activity

import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.uicore.fragment.ShortcutFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ShortcutActivity : BaseCoreAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            removeAll()
            show<ShortcutFragment>(ShortcutFragment::class.java) {
                arguments = intent.extras
            }
        }
    }
}