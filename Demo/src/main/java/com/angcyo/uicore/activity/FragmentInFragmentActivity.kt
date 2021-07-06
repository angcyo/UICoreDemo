package com.angcyo.uicore.activity

import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.base.dslChildFHelper
import com.angcyo.base.dslFHelper
import com.angcyo.base.log
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.uicore.demo.R

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class FragmentInFragmentActivity : BaseAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            show(FragmentParent())
        }
    }
}

class FragmentParent : AbsLifecycleFragment() {

    init {
        fragmentLayoutId = R.layout.fragment_parent
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.click(R.id.button_view) {
            dslChildFHelper {
                show(FragmentChild::class.java)
                show(FragmentChild::class.java)
                show(FragmentChild::class.java)
            }

            it.post {
                childFragmentManager.log()
            }
        }
    }
}

class FragmentChild : AbsLifecycleFragment() {

    init {
        fragmentLayoutId = R.layout.fragment_child
    }

}