package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dialog.singleColorPickerDialog
import com.angcyo.doodle.DoodleView
import com.angcyo.doodle.brush.*
import com.angcyo.doodle.core.DoodleUndoManager
import com.angcyo.doodle.core.IDoodleListener
import com.angcyo.doodle.core.Strategy
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.size
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.paintWidthPickerDialog
import com.angcyo.widget.span.span

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
                val doodleDelegate = doodleView?.doodleDelegate

                //
                doodleDelegate?.doodleListenerList?.add(object : IDoodleListener {
                    override fun onDoodleUndoChanged(undoManager: DoodleUndoManager) {
                        itemHolder.tv(R.id.undo_button)?.text = span {
                            append("撤销")
                            append("${undoManager.undoStack.size()}") {
                                isSuperscript = true
                            }
                        }
                        itemHolder.tv(R.id.redo_button)?.text = span {
                            append("重做")
                            append("${undoManager.redoStack.size()}") {
                                isSuperscript = true
                            }
                        }
                    }
                })

                val doodleTouchManager = doodleDelegate?.doodleTouchManager

                //
                itemHolder.click(R.id.clear_button) {
                    doodleDelegate?.operateLayer?.clearAllElement()
                }

                itemHolder.click(R.id.undo_button) {
                    doodleDelegate?.undoManager?.undo()
                }

                itemHolder.click(R.id.redo_button) {
                    doodleDelegate?.undoManager?.redo()
                }

                itemHolder.click(R.id.background_button) {
                    it.isSelected = !it.isSelected
                    doodleDelegate?.doodleLayerManager?.hideBackgroundLayer(
                        it.isSelected,
                        Strategy.Normal()
                    )
                }

                //大小
                itemHolder.tv(R.id.size_button)?.text =
                    "${doodleDelegate?.doodleConfig?.paintWidth}px"
                itemHolder.click(R.id.size_button) {
                    itemHolder.context.paintWidthPickerDialog {
                        initialWidth = doodleDelegate?.doodleConfig?.paintWidth ?: 20f
                        paintWidthResultAction = { dialog, width ->
                            doodleDelegate?.doodleConfig?.paintWidth = width
                            itemHolder.tv(R.id.size_button)?.text = "${width}px"
                            false
                        }
                    }
                }

                //颜色
                itemHolder.tv(R.id.color_button)?.setTextColor(
                    doodleDelegate?.doodleConfig?.paintColor ?: Color.BLACK
                )
                itemHolder.click(R.id.color_button) {
                    itemHolder.context.singleColorPickerDialog {
                        initialColor = doodleDelegate?.doodleConfig?.paintColor ?: Color.BLACK
                        colorPickerResultAction = { dialog, color ->
                            doodleDelegate?.doodleConfig?.paintColor = color
                            itemHolder.tv(R.id.color_button)?.setTextColor(color)
                            false
                        }
                    }
                }

                //
                itemHolder.click(R.id.eraser_button) {
                    doodleTouchManager?.updateTouchRecognize(EraserBrush())
                }

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

                itemHolder.click(R.id.zen_circle_button) {
                    doodleTouchManager?.updateTouchRecognize(ZenCircleBrush())
                }

                itemHolder.click(R.id.zen_path_button) {
                    doodleTouchManager?.updateTouchRecognize(ZenPathBrush())
                }
            }
        }
    }

}