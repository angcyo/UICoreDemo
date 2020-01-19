package com.angcyo.uicore.activity

import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import com.angcyo.base.dslFHelper
import com.angcyo.core.activity.BasePermissionsActivity
import com.angcyo.getData
import com.angcyo.uicore.fragment.TransitionDetailFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/19
 */

class TransitionDetailActivity : BasePermissionsActivity() {

    companion object {
        const val ANIM_EXPLODE = 1
        const val ANIM_FADE = 2
        const val ANIM_SLIDE = 3

        const val ANIM_EXIT = "anim_exit"
        const val ANIM_ENTER = "anim_enter"

        fun getTransitionAnim(anim: Int): Transition? {

            //分解（explode）：从场景中心移入或移出视图。
            //滑动（slide）：从场景边缘移入或移出视图。
            //淡入淡出（fade）：通过调整透明度在场景中增添或移除视图。

            return when (anim) {
                ANIM_EXPLODE -> Explode()
                ANIM_FADE -> Fade()
                ANIM_SLIDE -> Slide()
                else -> null
            }
        }

        fun getTransitionAnimx(anim: Int): androidx.transition.Transition? {
            return when (anim) {
                ANIM_EXPLODE -> androidx.transition.Explode()
                ANIM_FADE -> androidx.transition.Fade()
                ANIM_SLIDE -> androidx.transition.Slide()
                else -> null
            }
        }
    }

//    val transitionDetailFragment = TransitionDetailFragment()
//
//    override fun getActivityLayoutId(): Int {
//        return transitionDetailFragment.contentLayoutId
//    }

    fun getAnim(key: String): Transition? {
        val anim: Int = getData(key) ?: ANIM_EXPLODE
        return getTransitionAnim(anim)
    }

    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            restore(TransitionDetailFragment::class.java)
            configFragment {
                arguments = intent.extras
            }
        }

//        dslAHelper {
//            windowEnterTransition = getAnim(ANIM_ENTER)
//            windowExitTransition = getAnim(ANIM_EXIT)
//
//            transition {
//
//            }
//        }

        //移花接木
//        transitionDetailFragment.arguments = intent.extras
//        transitionDetailFragment.attachContext = this
//        transitionDetailFragment.baseViewHolder = _vh
//        transitionDetailFragment.onActivityCreated(null)
    }
}