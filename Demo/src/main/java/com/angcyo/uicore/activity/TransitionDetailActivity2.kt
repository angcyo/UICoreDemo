package com.angcyo.uicore.activity

import android.os.Bundle
import android.transition.ChangeBounds
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.uicore.demo.R

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/19
 */
class TransitionDetailActivity2 : BaseAppCompatActivity() {

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_transition_detail2
    }

    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        window.sharedElementEnterTransition = ChangeBounds()
    }
}