package com.angcyo.uicore.fragment

import android.os.Bundle
import android.transition.Transition
import com.angcyo.base.dslAHelper
import com.angcyo.getData
import com.angcyo.transition
import com.angcyo.uicore.activity.TransitionDetailActivity
import com.angcyo.uicore.activity.TransitionDetailActivity.Companion.ANIM_ENTER
import com.angcyo.uicore.activity.TransitionDetailActivity.Companion.ANIM_EXIT
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.demo.TransitionDemo

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/19
 */
class TransitionDetailFragment : TransitionDemo() {
    init {
        contentLayoutId = R.layout.fragment_transition_detail
    }

    override fun enableBackItem(): Boolean {
        return true
    }

    fun getAnim(key: String): Transition? {
        val anim: Int = getData(key) ?: TransitionDetailActivity.ANIM_EXPLODE
        return TransitionDetailActivity.getTransitionAnim(anim)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dslAHelper {
            windowEnterTransition = getAnim(ANIM_ENTER)
            windowExitTransition = getAnim(ANIM_EXIT)

            transition {
                transition(_vh.view(R.id.logo))
                transition(_vh.view(R.id.to_activity))
                transition(_vh.view(R.id.to_activity_share), "button2")
            }
        }
    }
}