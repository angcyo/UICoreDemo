package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.doodle.DoodleView
import com.angcyo.doodle.brush.*
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/07/25
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DoodleDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.doodle_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val doodleView = itemHolder.v<DoodleView>(R.id.doodle_view)

                val doodleTouchManager = doodleView?.doodleDelegate?.doodleTouchManager

                //
                itemHolder.click(R.id.clear_button) {
                    doodleView?.doodleDelegate?.operateLayer?.clearAllElement()
                }

                itemHolder.click(R.id.undo_button) {
                    doodleView?.doodleDelegate?.undoManager?.undo()
                }

                itemHolder.click(R.id.redo_button) {
                    doodleView?.doodleDelegate?.undoManager?.redo()
                }

                //
                itemHolder.click(R.id.normal_button) {
                    doodleTouchManager?.updateTouchRecognize(NormalBrush())
                }

                itemHolder.click(R.id.debug_button) {
                    doodleTouchManager?.updateTouchRecognize(DebugBrush())
                }

                itemHolder.click(R.id.pen_button) {
                    doodleTouchManager?.updateTouchRecognize(PenBrush())
                }

                itemHolder.click(R.id.zen_button) {
                    doodleTouchManager?.updateTouchRecognize(ZenBrush())
                }

                itemHolder.click(R.id.zen_oval_button) {
                    doodleTouchManager?.updateTouchRecognize(ZenOvalBrush())
                }

            }
        }
    }

}