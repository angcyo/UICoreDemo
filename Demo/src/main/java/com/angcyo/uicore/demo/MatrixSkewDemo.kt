package com.angcyo.uicore.demo

import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.*
import com.angcyo.library.unit.unitDecimal
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.BaseMatrixView
import com.angcyo.uicore.demo.draw.DrawSkewView
import com.angcyo.uicore.demo.draw.IMatrixView
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.progress.DslProgressBar
import com.angcyo.widget.progress.DslSeekBar

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/01/09
 */
open class MatrixSkewDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_matrix_skew) { itemHolder, itemPosition, adapterItem, payloads ->
                val demoView = itemHolder.v<DrawSkewView>(R.id.draw_skew_view)

                //---rotate
                bindRotate(itemHolder, demoView)

                //---scale
                bindScale(itemHolder, demoView)

                //---skew
                bindSkew(itemHolder, demoView)
            }
        }
    }

    fun updateResult(itemHolder: DslViewHolder) {
        val drawSkewView = itemHolder.v<DrawSkewView>(R.id.draw_skew_view)
        drawSkewView?.let {
            val groupMatrix = it.groupMatrix()
            val subMatrix = it.subMatrix()

            val matrix = Matrix()
            matrix.postScale(it.groupScaleX, it.groupScaleY)
            matrix.postRotate(it.subRotate)

            val denom = Math.pow(it.groupScaleX + 0.0, 2.0) + Math.pow(matrix.getSkewX() + 0.0, 2.0)
            val skewX = Math.atan2(
                it.groupScaleX * matrix.getSkewY() * 1.0 +
                        it.groupScaleY * matrix.getSkewX() * 1.0,
                denom
            )
            itemHolder.tv(R.id.result_text_view)?.text = buildString {
                appendLine("GroupRotate:${groupMatrix.getRotateDegrees()} y:${groupMatrix.getRotateDegreesY()} SubRotate:${subMatrix.getRotateDegrees()} y:${subMatrix.getRotateDegreesY()} ${it.subRotate}")
                appendLine("1:${subMatrix.toLogString()}")
                appendLine("↓")
                //--
                appendLine(it.groupScaleX * it.subRotate.toRadians()) //a tan
                appendLine("$skewX")
                appendLine("1:${matrix.toLogString()}")
                appendLine("2:${it.subMatrix.toLogString()}")
                appendLine("3:${it.subMatrix2.toLogString()}")
            }
        }
    }

    //---
    fun bindAnchor(itemHolder: DslViewHolder, view: BaseMatrixView?) {
        itemHolder.click(R.id.anchor_x_label) {
            view?.updateSubAnchor(0f)
        }
        itemHolder.v<DslSeekBar>(R.id.anchor_x_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatRotateTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    if (fromUser) {
                        view?.updateSubAnchor(0f)
                    }
                }
            }
        }

        itemHolder.click(R.id.anchor_y_label) {
            view?.updateSubAnchor(y = 0f)
        }
        itemHolder.v<DslSeekBar>(R.id.anchor_y_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatRotateTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    if (fromUser) {
                        view?.updateSubAnchor(y = 0f)
                    }
                }
            }
        }
    }

    //---

    fun bindRotate(itemHolder: DslViewHolder, view: BaseMatrixView?) {
        itemHolder.click(R.id.group_rotate_label) {
            //view?.groupRotate = 0f
            view?.updateGroupRect()
        }
        itemHolder.v<DslSeekBar>(R.id.group_rotate_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatRotateTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    //view?.groupRotate = formatRotateValue(fraction)
                    if (fromUser) {
                        view?.rotateGroupTo(formatRotateValue(fraction))
                        updateResult(itemHolder)
                    }
                    //itemHolder.v<DslSeekBar>(R.id.rotate_seek)?.setProgress()
                }
            }
        }

        itemHolder.click(R.id.rotate_label) {
            view?.subRotateTo(0f)
        }
        itemHolder.v<DslSeekBar>(R.id.rotate_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatRotateTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    if (fromUser) {
                        view?.subRotateTo(formatRotateValue(fraction))
                        updateResult(itemHolder)
                    }
                }
            }
        }
    }

    fun bindScale(itemHolder: DslViewHolder, view: BaseMatrixView?) {
        itemHolder.click(R.id.scale_x_label) {
            view?.scaleGroupTo2(1f)
        }
        itemHolder.v<DslSeekBar>(R.id.scale_x_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatScaleTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    view?.scaleGroupTo2(fraction)
                    updateResult(itemHolder)
                }
            }
        }
        itemHolder.click(R.id.scale_y_label) {
            view?.scaleGroupTo2(sy = 1f)
        }
        itemHolder.v<DslSeekBar>(R.id.scale_y_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatScaleTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    view?.scaleGroupTo2(sy = fraction)
                    updateResult(itemHolder)
                }
            }
        }

        //---

        itemHolder.click(R.id.sub_scale_x_label) {
            view?.scaleSubTo(1f)
        }
        itemHolder.v<DslSeekBar>(R.id.sub_scale_x_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatScaleTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    if (fromUser) {
                        view?.scaleSubTo(fraction)
                        updateResult(itemHolder)
                    }
                }
            }
        }
        itemHolder.click(R.id.sub_scale_y_label) {
            view?.scaleSubTo(sy = 1f)
        }
        itemHolder.v<DslSeekBar>(R.id.sub_scale_y_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatScaleTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    if (fromUser) {
                        view?.scaleSubTo(sy = fraction)
                        updateResult(itemHolder)
                    }
                }
            }
        }
    }

    fun bindSkew(itemHolder: DslViewHolder, view: BaseMatrixView?) {
        itemHolder.click(R.id.skew_x_label) {
            view?.skewSubTo(1f)
        }
        itemHolder.v<DslSeekBar>(R.id.skew_x_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatSkewTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    if (fromUser) {
                        view?.skewSubTo(formatSkewValue(fraction))
                        updateResult(itemHolder)
                    }
                }
            }
        }
        itemHolder.click(R.id.skew_y_label) {
            view?.skewSubTo(ky = 0f)
        }
        itemHolder.v<DslSeekBar>(R.id.skew_y_seek)?.apply {
            progressTextFormatAction = this@MatrixSkewDemo::formatSkewTextAction
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    if (fromUser) {
                        view?.skewSubTo(ky = formatSkewValue(fraction))
                        updateResult(itemHolder)
                    }
                }
            }
        }

        if (view is View) {
            itemHolder.click(view) {
                updateScaleSkewToViewResult(itemHolder, view)
            }
        }
    }

    //---

    fun formatSkewValue(fraction: Float): Float {
        val min = -180f
        val max = 180f
        val target = min + (max - min) * fraction
        return target
    }

    fun formatRotateValue(fraction: Float): Float {
        val min = 0f
        val max = 360f
        val target = min + (max - min) * fraction
        return target
    }

    fun formatScaleTextAction(bar: DslProgressBar): String =
        (bar.progressValue / 100f).unitDecimal()

    fun formatSkewTextAction(bar: DslProgressBar): String =
        formatSkewValue(bar.progressValue / 100f).unitDecimal()

    fun formatRotateTextAction(bar: DslProgressBar): String =
        formatRotateValue(bar.progressValue / 100f).unitDecimal()

    fun updateScaleSkewToViewResult(itemHolder: DslViewHolder, view: IMatrixView?) {
        view ?: return
        itemHolder.v<DslSeekBar>(R.id.rotate_seek)
            ?.setProgress(view.subRotate / 360f * 100)

        itemHolder.v<DslSeekBar>(R.id.sub_scale_x_seek)?.setProgress(view.subScaleX * 100)
        itemHolder.v<DslSeekBar>(R.id.sub_scale_y_seek)?.setProgress(view.subScaleY * 100)

        itemHolder.v<DslSeekBar>(R.id.skew_x_seek)
            ?.setProgress((view.subSkewX + 180) / 360f * 100)
        itemHolder.v<DslSeekBar>(R.id.skew_y_seek)
            ?.setProgress((view.subSkewY + 180) / 360f * 100)
    }

    //---

}