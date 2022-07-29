package com.angcyo.uicore.demo

import android.graphics.Matrix
import android.os.Bundle
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.*
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.DrawImageView
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.base.string

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/07
 */
class MatrixDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_matrix) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.hawkInstallAndRestore("matrix_")

                //clear
                itemHolder.click(R.id.clear_button) {
                    itemHolder.ev(R.id.l_edit)?.setInputText("0")
                    itemHolder.ev(R.id.t_edit)?.setInputText("0")
                    itemHolder.ev(R.id.r_edit)?.setInputText("100")
                    itemHolder.ev(R.id.b_edit)?.setInputText("100")
                    itemHolder.updateMatrix(Matrix())
                    itemHolder.updateText()
                }

                //invert
                itemHolder.click(R.id.invert_button) {
                    itemHolder.tv(R.id.text_view)?.text = buildString {
                        appendLine(itemHolder.editMatrix().invert(_tempMatrix))
                        append(_tempMatrix.toLogString())
                        append(itemHolder.mapString())
                    }
                }

                itemHolder.click(R.id.invert_and_set_button) {
                    itemHolder.tv(R.id.text_view)?.text = buildString {
                        appendLine(itemHolder.editMatrix().invert(_tempMatrix))
                        append(_tempMatrix.toLogString())
                        append(itemHolder.mapString())
                        itemHolder.updateMatrix(_tempMatrix)
                    }
                }

                //map
                itemHolder.click(R.id.map_button) {
                    itemHolder.tv(R.id.text_view)?.text = buildString {
                        append(itemHolder.editMatrix().toLogString())
                        append(itemHolder.mapString())
                    }
                }
                itemHolder.click(R.id.map_and_set_button) {
                    itemHolder.tv(R.id.text_view)?.text = buildString {
                        append(itemHolder.editMatrix().toLogString())
                        append(itemHolder.mapString())
                        itemHolder.ev(R.id.l_edit)?.setInputText("${_tempPoint.x}")
                        itemHolder.ev(R.id.t_edit)?.setInputText("${_tempPoint.y}")
                    }
                }

                //scale
                itemHolder.click(R.id.set_scale_button) {
                    itemHolder.editMatrix().apply {
                        setScale(
                            itemHolder.ev(R.id.set_scale_x_edit).string().toFloatOrNull() ?: 1f,
                            itemHolder.ev(R.id.set_scale_y_edit).string().toFloatOrNull() ?: 1f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.pre_scale_button) {
                    itemHolder.editMatrix().apply {
                        preScale(
                            itemHolder.ev(R.id.set_scale_x_edit).string().toFloatOrNull() ?: 1f,
                            itemHolder.ev(R.id.set_scale_y_edit).string().toFloatOrNull() ?: 1f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.post_scale_button) {
                    itemHolder.editMatrix().apply {
                        postScale(
                            itemHolder.ev(R.id.set_scale_x_edit).string().toFloatOrNull() ?: 1f,
                            itemHolder.ev(R.id.set_scale_y_edit).string().toFloatOrNull() ?: 1f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }

                //trans
                itemHolder.click(R.id.set_trans_button) {
                    itemHolder.editMatrix().apply {
                        setTranslate(
                            itemHolder.ev(R.id.set_trans_x_edit).string().toFloatOrNull() ?: 0f,
                            itemHolder.ev(R.id.set_trans_y_edit).string().toFloatOrNull() ?: 0f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.pre_trans_button) {
                    itemHolder.editMatrix().apply {
                        preTranslate(
                            itemHolder.ev(R.id.set_trans_x_edit).string().toFloatOrNull() ?: 0f,
                            itemHolder.ev(R.id.set_trans_y_edit).string().toFloatOrNull() ?: 0f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.post_trans_button) {
                    itemHolder.editMatrix().apply {
                        postTranslate(
                            itemHolder.ev(R.id.set_trans_x_edit).string().toFloatOrNull() ?: 0f,
                            itemHolder.ev(R.id.set_trans_y_edit).string().toFloatOrNull() ?: 0f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }

                //skew
                itemHolder.click(R.id.set_skew_button) {
                    itemHolder.editMatrix().apply {
                        setSkew(
                            itemHolder.ev(R.id.set_skew_x_edit).string().toFloatOrNull() ?: 0f,
                            itemHolder.ev(R.id.set_skew_y_edit).string().toFloatOrNull() ?: 0f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.pre_skew_button) {
                    itemHolder.editMatrix().apply {
                        preSkew(
                            itemHolder.ev(R.id.set_skew_x_edit).string().toFloatOrNull() ?: 0f,
                            itemHolder.ev(R.id.set_skew_y_edit).string().toFloatOrNull() ?: 0f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.post_skew_button) {
                    itemHolder.editMatrix().apply {
                        postSkew(
                            itemHolder.ev(R.id.set_skew_x_edit).string().toFloatOrNull() ?: 0f,
                            itemHolder.ev(R.id.set_skew_y_edit).string().toFloatOrNull() ?: 0f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }

                //rotate
                itemHolder.click(R.id.set_rotate_button) {
                    itemHolder.editMatrix().apply {
                        setRotate(
                            itemHolder.ev(R.id.set_rotate_edit).string().toFloatOrNull() ?: 45f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.pre_rotate_button) {
                    itemHolder.editMatrix().apply {
                        preRotate(
                            itemHolder.ev(R.id.set_rotate_edit).string().toFloatOrNull() ?: 45f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
                itemHolder.click(R.id.post_rotate_button) {
                    itemHolder.editMatrix().apply {
                        postRotate(
                            itemHolder.ev(R.id.set_rotate_edit).string().toFloatOrNull() ?: 45f
                        )
                        itemHolder.updateMatrix(this)
                        itemHolder.updateText()
                    }
                }
            }
        }
    }

    fun DslViewHolder.editMatrix(): Matrix {
        val matrix = Matrix()
        _tempValues[Matrix.MSCALE_X] = ev(R.id.scale_x_edit).string().toFloatOrNull() ?: 1f
        _tempValues[Matrix.MSCALE_Y] = ev(R.id.scale_y_edit).string().toFloatOrNull() ?: 1f

        _tempValues[Matrix.MSKEW_X] = ev(R.id.skew_x_edit).string().toFloatOrNull() ?: 0f
        _tempValues[Matrix.MSKEW_Y] = ev(R.id.skew_y_edit).string().toFloatOrNull() ?: 0f

        _tempValues[Matrix.MTRANS_X] = ev(R.id.trans_x_edit).string().toFloatOrNull() ?: 0f
        _tempValues[Matrix.MTRANS_Y] = ev(R.id.trans_y_edit).string().toFloatOrNull() ?: 0f

        _tempValues[Matrix.MPERSP_0] = ev(R.id.persp_0_edit).string().toFloatOrNull() ?: 0f
        _tempValues[Matrix.MPERSP_1] = ev(R.id.persp_1_edit).string().toFloatOrNull() ?: 0f
        _tempValues[Matrix.MPERSP_2] = ev(R.id.persp_2_edit).string().toFloatOrNull() ?: 0f

        matrix.setValues(_tempValues)
        return matrix
    }

    fun DslViewHolder.updateMatrix(matrix: Matrix) {
        matrix.getValues(_tempValues)

        ev(R.id.scale_x_edit)?.setInputText("${_tempValues[Matrix.MSCALE_X]}")
        ev(R.id.scale_y_edit)?.setInputText("${_tempValues[Matrix.MSCALE_Y]}")

        ev(R.id.skew_x_edit)?.setInputText("${_tempValues[Matrix.MSKEW_X]}")
        ev(R.id.skew_y_edit)?.setInputText("${_tempValues[Matrix.MSKEW_Y]}")

        ev(R.id.trans_x_edit)?.setInputText("${_tempValues[Matrix.MTRANS_X]}")
        ev(R.id.trans_y_edit)?.setInputText("${_tempValues[Matrix.MTRANS_Y]}")

        ev(R.id.persp_0_edit)?.setInputText("${_tempValues[Matrix.MPERSP_0]}")
        ev(R.id.persp_1_edit)?.setInputText("${_tempValues[Matrix.MPERSP_1]}")
        ev(R.id.persp_2_edit)?.setInputText("${_tempValues[Matrix.MPERSP_2]}")

        //img
        v<DrawImageView>(R.id.image_view)?.apply {
            drawableMatrix = matrix
        }
    }

    fun DslViewHolder.mapString(): String = buildString {
        appendLine()
        _tempPoint.x = ev(R.id.l_edit).string().toFloatOrNull() ?: 100f
        _tempPoint.y = ev(R.id.t_edit).string().toFloatOrNull() ?: 100f

        val r = ev(R.id.r_edit).string().toFloatOrNull() ?: 200f
        val b = ev(R.id.b_edit).string().toFloatOrNull() ?: 200f

        _tempRectF.set(_tempPoint.x, _tempPoint.y, r, b)

        append(_tempPoint)
        append(" -> ")
        append(editMatrix().mapPoint(_tempPoint))
        appendLine()
        append(_tempRectF)
        append(" -> ")
        append(editMatrix().mapRectF(_tempRectF))
    }

    fun Matrix.toLogString(): String = buildString {

        appendLine()
        val rotate = getRotateDegrees()
        appendLine("rotate:${rotate}° ${rotate.toRadians()}")
        appendLine("isAffine:${isAffine}")
        appendLine("isIdentity:${isIdentity}")
        appendLine()

        getValues(_tempValues)
        for (i in 0 until 9) {
            if (i % 3 == 0) {
                append("[")
            }
            append(_tempValues[i])
            if (i % 3 != 2) {
                append(", ")
            }
            if (i % 3 == 2) {
                append("]")
                appendLine()
            }
        }
    }

    /**更新输出文本*/
    fun DslViewHolder.updateText(matrix: Matrix = editMatrix()) {
        tv(R.id.text_view)?.text = buildString {
            append(matrix.toLogString())
            append(mapString())
        }
    }
}