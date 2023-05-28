package com.angcyo.uicore.demo

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import com.angcyo.component.getPhoto
import com.angcyo.component.luban.luban
import com.angcyo.dialog.singleColorPickerDialog
import com.angcyo.doodle.DoodleView
import com.angcyo.doodle.brush.*
import com.angcyo.doodle.core.DoodleUndoManager
import com.angcyo.doodle.core.IDoodleListener
import com.angcyo.doodle.data.BitmapElementData
import com.angcyo.doodle.element.BitmapElement
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.*
import com.angcyo.library.component.Strategy
import com.angcyo.library.component.pad.isInPadMode
import com.angcyo.library.ex.*
import com.angcyo.library.model.loadPath
import com.angcyo.library.utils.fileNameUUID
import com.angcyo.picker.dslSinglePickerImage
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.paintWidthPickerDialog
import com.angcyo.widget.span.span
import kotlin.math.max

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
            bindItem(R.layout.demo_doodle_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val doodleView = itemHolder.v<DoodleView>(R.id.doodle_view)
                val doodleDelegate = doodleView?.doodleDelegate

                if (isInPadMode()) {
                    doodleView?.setHeight(max(_screenWidth, _screenHeight) / 3)
                }

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
                        Strategy.normal
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
                itemHolder.click(R.id.mosaic_button) {
                    doodleTouchManager?.updateTouchRecognize(MosaicBrush())
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

                itemHolder.click(R.id.bitmap_brush_button) {
                    //替换画刷
                    doodleDelegate?.doodleConfig?.brushBitmap = BitmapFactory.decodeResource(
                        fContext().resources,
                        when (Library.CLICK_COUNT++ % 4) {
                            1 -> R.mipmap.hua
                            2 -> R.mipmap.huaduo
                            3 -> R.mipmap.yanhua
                            else -> R.mipmap.brush
                        }
                    )
                    //
                    doodleTouchManager?.updateTouchRecognize(BitmapBrush())
                }

                //image
                itemHolder.click(R.id.image_button) {
                    if (isDebugType() && Library.CLICK_COUNT++ % 2 == 0) {
                        dslSinglePickerImage {
                            it?.firstOrNull()?.let { media ->
                                media.loadPath()?.apply {
                                    doodleDelegate?.addElement(BitmapElement(BitmapElementData().apply {
                                        updateBitmap(doodleDelegate, toBitmap())
                                    }))
                                }
                            }
                        }
                    } else {
                        getPhoto {
                            val path = libCacheFile(fileNameUUID(".png")).absolutePath
                            it?.save(path)
                            val newPath = path.luban()
                            L.i("${path}->${newPath}")

                            //压缩后
                            newPath.toBitmap()?.let {
                                doodleDelegate?.addElement(BitmapElement(BitmapElementData().apply {
                                    updateBitmap(doodleDelegate, it)
                                }))
                            }
                        }
                    }
                }
            }
        }
    }

}