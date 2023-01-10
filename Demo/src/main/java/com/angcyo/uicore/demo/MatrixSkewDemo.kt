package com.angcyo.uicore.demo

import android.graphics.Matrix
import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.getSkewX
import com.angcyo.library.ex.getSkewY
import com.angcyo.library.ex.toLogString
import com.angcyo.library.ex.toRadians
import com.angcyo.library.unit.unitDecimal
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.DrawSkewView
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.progress.DslProgressBar
import com.angcyo.widget.progress.DslSeekBar

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/01/09
 */
class MatrixSkewDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_matrix_skew) { itemHolder, itemPosition, adapterItem, payloads ->
                val drawSkewView = itemHolder.v<DrawSkewView>(R.id.draw_skew_view)

                itemHolder.click(R.id.rotate_label) {
                    drawSkewView?.subRotate = 0f
                    drawSkewView?.invalidate()
                }
                itemHolder.v<DslSeekBar>(R.id.rotate_seek)?.apply {
                    progressTextFormatAction = this@MatrixSkewDemo::formatRotateTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            drawSkewView?.subRotate = formatRotateValue(fraction)
                            drawSkewView?.invalidate()
                            updateResult(itemHolder)
                        }
                    }
                }

                //---
                itemHolder.click(R.id.scale_x_label) {
                    drawSkewView?.groupScaleX = 1f
                    drawSkewView?.invalidate()
                }
                itemHolder.v<DslSeekBar>(R.id.scale_x_seek)?.apply {
                    progressTextFormatAction = this@MatrixSkewDemo::formatScaleTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            drawSkewView?.groupScaleX = fraction
                            drawSkewView?.invalidate()
                            updateResult(itemHolder)
                        }
                    }
                }
                itemHolder.click(R.id.scale_y_label) {
                    drawSkewView?.groupScaleY = 1f
                    drawSkewView?.invalidate()
                }
                itemHolder.v<DslSeekBar>(R.id.scale_y_seek)?.apply {
                    progressTextFormatAction = this@MatrixSkewDemo::formatScaleTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            drawSkewView?.groupScaleY = fraction
                            drawSkewView?.invalidate()
                            updateResult(itemHolder)
                        }
                    }
                }
                //---
                itemHolder.click(R.id.skew_x_label) {
                    drawSkewView?.subSkewX = 0f
                    drawSkewView?.invalidate()
                }
                itemHolder.v<DslSeekBar>(R.id.skew_x_seek)?.apply {
                    progressTextFormatAction = this@MatrixSkewDemo::formatSkewTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            drawSkewView?.subSkewX = formatSkewValue(fraction)
                            drawSkewView?.invalidate()
                            updateResult(itemHolder)
                        }
                    }
                }
                itemHolder.click(R.id.skew_y_label) {
                    drawSkewView?.subSkewY = 0f
                    drawSkewView?.invalidate()
                }
                itemHolder.v<DslSeekBar>(R.id.skew_y_seek)?.apply {
                    progressTextFormatAction = this@MatrixSkewDemo::formatSkewTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            drawSkewView?.subSkewY = formatSkewValue(fraction)
                            drawSkewView?.invalidate()
                            updateResult(itemHolder)
                        }
                    }
                }
            }
        }
    }

    fun updateResult(itemHolder: DslViewHolder) {
        val drawSkewView = itemHolder.v<DrawSkewView>(R.id.draw_skew_view)
        drawSkewView?.let {
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
                appendLine(it.groupScaleX * it.subRotate.toRadians()) //a tan
                appendLine("$skewX") //a tan
                appendLine("1:${matrix.toLogString()}") //a tan
                appendLine("2:${it.subMatrix.toLogString()}") //a tan
                appendLine("3:${it.subMatrix2.toLogString()}") //a tan
            }
        }
    }

    fun formatSkewValue(fraction: Float): Float {
        val min = -1f
        val max = 1f
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

}