package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.transition.*
import com.angcyo.base.dslAHelper
import com.angcyo.library.ex.*
import com.angcyo.noAnim
import com.angcyo.putData
import com.angcyo.transition
import com.angcyo.transition.dslTransition
import com.angcyo.uicore.activity.TransitionDetailActivity
import com.angcyo.uicore.activity.TransitionDetailActivity2
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.base.clickIt
import com.angcyo.widget.base.frameParams
import com.angcyo.widget.base.inflate
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

        //transition
        _vh.click(R.id.transition_button) {
            dslTransition(_vh.group(R.id.transition_layout)) {
                onCaptureEndValues = {
                    if (it.isSelected) {
                        it.setHeight(100 * dpi)
                        it.isSelected = false
                        _vh.img(R.id.transition_image)?.apply {
                            setImageResource(R.drawable.ic_logo_small)
                            frameParams {
                                gravity = Gravity.RIGHT or Gravity.BOTTOM
                            }
                        }
                        _vh.view(R.id.transition_button)?.apply {
                            frameParams {
                                gravity = Gravity.CENTER
                            }
                        }
                    } else {
                        it.setHeight(150 * dpi)
                        it.isSelected = true
                        _vh.img(R.id.transition_image)?.apply {
                            setImageResource(R.drawable.ic_logo)
                            frameParams {
                                gravity = Gravity.CENTER
                            }
                        }
                        _vh.view(R.id.transition_button)?.apply {
                            frameParams {
                                topMargin = 0
                                gravity = Gravity.LEFT or Gravity.TOP
                            }
                        }
                    }
                }
            }
        }

        //scene
        _vh.postDelay(1_000) {
            toScene1()
        }
    }

    fun toScene1() {
        dslTransition(_vh.group(R.id.scene_layout), R.layout.scene_layout1) {
            onSetTransition = {
                TransitionSet().apply {
                    TransitionDetailActivity.getTransitionAnimX(animExit)
                        ?.run { addTransition(this) }
                    addTransition(ChangeBounds())
                    addTransition(ChangeTransform())
                    addTransition(ChangeClipBounds())
                    addTransition(ChangeImageTransform())
                }
            }
            onSceneEnter = {
                it.find<View>(R.id.to_scene2)?.clickIt {
                    toScene2()
                }
            }
        }
    }

    fun toScene2() {
        dslTransition(_vh.group(R.id.scene_layout), R.layout.scene_layout2) {
            onSetTransition = {
                TransitionSet().apply {
                    TransitionDetailActivity.getTransitionAnimX(animEnter)
                        ?.run { addTransition(this) }
                    addTransition(ChangeBounds())
                    addTransition(ChangeTransform())
                    addTransition(ChangeClipBounds())
                    addTransition(ChangeImageTransform())
                }
            }
            onSceneEnter = {
                it.find<View>(R.id.to_scene1)?.clickIt {
                    toScene1()
                }
                it.find<View>(R.id.to_scene3)?.clickIt {
                    toScene3()
                }
            }
        }
    }

    fun toScene3() {
        _vh.group(R.id.scene_layout)?.apply {
            dslTransition(this, SceneLayout3(this))
        }
    }
}

class SceneLayout1(val viewGroup: ViewGroup) : Scene(viewGroup) {
    override fun enter() {
        //必须要removeAllViews才有动画
        viewGroup.removeAllViews()
        viewGroup.inflate(R.layout.scene_layout1)
        super.enter()

        viewGroup.find<View>(R.id.to_scene2)?.clickIt {
            dslTransition(viewGroup, SceneLayout2(viewGroup))
        }
    }
}

class SceneLayout2(val viewGroup: ViewGroup) : Scene(viewGroup) {
    override fun enter() {
        viewGroup.removeAllViews()
        viewGroup.inflate(R.layout.scene_layout2)
        super.enter()

        viewGroup.find<View>(R.id.to_scene1)?.clickIt {
            dslTransition(viewGroup, SceneLayout1(viewGroup))
        }
        viewGroup.find<View>(R.id.to_scene3)?.clickIt {
            dslTransition(viewGroup, SceneLayout3(viewGroup))
        }
    }
}

class SceneLayout3(val viewGroup: ViewGroup) : Scene(viewGroup) {
    override fun enter() {
        viewGroup.removeAllViews()
        viewGroup.inflate(R.layout.scene_layout3)
        super.enter()

        viewGroup.find<View>(R.id.to_scene1)?.clickIt {
            dslTransition(viewGroup, SceneLayout1(viewGroup))
        }
    }
}