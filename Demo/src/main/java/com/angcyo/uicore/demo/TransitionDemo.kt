package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.base.dslAHelper
import com.angcyo.library.ex.hawkGet
import com.angcyo.library.ex.hawkPut
import com.angcyo.noAnim
import com.angcyo.putData
import com.angcyo.transition
import com.angcyo.uicore.activity.TransitionDetailActivity
import com.angcyo.uicore.activity.TransitionDetailActivity2
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.spinner

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/19
 */

open class TransitionDemo : AppTitleFragment() {
    init {
        contentLayoutId = R.layout.demo_transition
    }

    var animEnter = TransitionDetailActivity.ANIM_ENTER.hawkGet()?.toIntOrNull() ?: 1
    var animExit = TransitionDetailActivity.ANIM_EXIT.hawkGet()?.toIntOrNull() ?: 1

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _vh.spinner(R.id.enter_spinner)?.apply {
            setStrings(listOf("NONE", "EXPLODE", "FADE", "SLIDE")) {
                animEnter = it
                TransitionDetailActivity.ANIM_ENTER.hawkPut(it.toString())
            }
            setSelection(animEnter)
        }
        _vh.spinner(R.id.exit_spinner)?.apply {
            setStrings(listOf("NONE", "EXPLODE", "FADE", "SLIDE")) {
                animExit = it
                TransitionDetailActivity.ANIM_EXIT.hawkPut(it.toString())
            }
            setSelection(animExit)
        }

        _vh.click(R.id.to_activity) {
            dslAHelper {
                windowEnterTransition = TransitionDetailActivity.getTransitionAnim(animEnter)
                windowExitTransition = TransitionDetailActivity.getTransitionAnim(animExit)

                start(TransitionDetailActivity::class.java) {
                    putData(animEnter, TransitionDetailActivity.ANIM_ENTER)
                    putData(animExit, TransitionDetailActivity.ANIM_EXIT)
                    noAnim()
                }
            }
        }

        _vh.click(R.id.to_activity_share) {
            dslAHelper {
                windowEnterTransition = TransitionDetailActivity.getTransitionAnim(animEnter)
                windowExitTransition = TransitionDetailActivity.getTransitionAnim(animExit)

                start(TransitionDetailActivity::class.java) {
                    putData(animEnter, TransitionDetailActivity.ANIM_ENTER)
                    putData(animExit, TransitionDetailActivity.ANIM_EXIT)

                    transition(_vh.view(R.id.logo))
                    transition(_vh.view(R.id.to_activity))
                    transition(_vh.view(R.id.to_activity_share), "button2")

                    noAnim()
                }
            }
        }

        _vh.click(R.id.to_activity2) {
            dslAHelper {
                start(TransitionDetailActivity2::class.java) {
                    transition(_vh.view(R.id.logo))
                    transition(_vh.view(R.id.to_activity))
                    transition(_vh.view(R.id.to_activity_share), "button2")
                }
            }
        }
    }
}