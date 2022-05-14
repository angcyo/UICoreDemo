package com.angcyo.uicore.demo.canvas

import android.graphics.PointF
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.core.IRenderer
import com.angcyo.canvas.items.renderer.BaseItemRenderer
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.decimal
import com.angcyo.library.ex.visible
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/14
 */
class CanvasEditControlItem : DslAdapterItem() {

    var itemRenderer: IRenderer? = null

    var itemCanvasDelegate: CanvasDelegate? = null

    val _tempPoint = PointF()

    init {
        itemLayoutId = R.layout.item_canvas_edit_control_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        val renderer = itemRenderer
        val canvasDelegate = itemCanvasDelegate
        if (canvasDelegate != null && renderer is BaseItemRenderer<*>) {
            val drawable = renderer._rendererItem?.itemDrawable ?: itemRenderer?.preview()
            itemHolder.img(R.id.item_drawable_view)?.apply {
                visible(drawable != null)
                itemHolder.visible(R.id.item_drawable_line_view, drawable != null)
                setImageDrawable(drawable)
            }
            itemHolder.selected(R.id.item_lock_view, renderer.isLockScaleRatio)

            val canvasViewBox = canvasDelegate.getCanvasViewBox()
            //宽高
            val rotateBounds = renderer.getRotateBounds()
            val width =
                canvasViewBox.valueUnit.convertPixelToValue(rotateBounds.width()).decimal(2)
            val height =
                canvasViewBox.valueUnit.convertPixelToValue(rotateBounds.height()).decimal(2)

            itemHolder.tv(R.id.item_width_view)?.text = "$width"
            itemHolder.tv(R.id.item_height_view)?.text = "$height"

            //xy坐标
            _tempPoint.set(rotateBounds.left, rotateBounds.top)
            val point = _tempPoint
            val value = canvasViewBox.calcDistanceValueWithOrigin(point)

            val x = value.x.decimal(2)
            val y = value.y.decimal(2)

            itemHolder.tv(R.id.item_axis_x_view)?.text = "$x"
            itemHolder.tv(R.id.item_axis_y_view)?.text = "$y"

            //旋转
            itemHolder.tv(R.id.item_rotate_view)?.text = "${renderer.rotate.decimal(2)}°"

            //等比

            bindWidthHeight(itemHolder)
            bindAxis(itemHolder)
            bindRotate(itemHolder)
        } else {
            itemHolder.gone(R.id.item_drawable_view)
            itemHolder.gone(R.id.item_drawable_line_view)
            itemHolder.tv(R.id.item_width_view)?.text = null
            itemHolder.tv(R.id.item_height_view)?.text = null
            itemHolder.tv(R.id.item_axis_x_view)?.text = null
            itemHolder.tv(R.id.item_axis_y_view)?.text = null
            itemHolder.tv(R.id.item_rotate_view)?.text = null
        }
    }

    fun DslViewHolder.isLockRatio() = view(R.id.item_lock_view)?.isSelected == true

    /**绑定宽高事件*/
    fun bindWidthHeight(itemHolder: DslViewHolder) {
        itemHolder.click(R.id.item_width_view) {
            val renderer = itemRenderer
            if (renderer is BaseItemRenderer<*>) {
                itemHolder.context.canvasNumberWindow(it) {
                    onClickNumberAction = {

                        false
                    }
                }
            }
        }
        itemHolder.click(R.id.item_height_view) {
            val renderer = itemRenderer
            if (renderer is BaseItemRenderer<*>) {
                itemHolder.context.canvasNumberWindow(it) {
                    onClickNumberAction = {

                        false
                    }
                }
            }
        }
    }

    /**绑定xy轴事件*/
    fun bindAxis(itemHolder: DslViewHolder) {
        itemHolder.click(R.id.item_axis_x_view) {
            val renderer = itemRenderer
            if (renderer is BaseItemRenderer<*>) {
                itemHolder.context.canvasNumberWindow(it) {
                    onClickNumberAction = {

                        false
                    }
                }
            }
        }
        itemHolder.click(R.id.item_axis_y_view) {
            val renderer = itemRenderer
            if (renderer is BaseItemRenderer<*>) {
                itemHolder.context.canvasNumberWindow(it) {
                    onClickNumberAction = {

                        false
                    }
                }
            }
        }
    }

    /**绑定旋转事件*/
    fun bindRotate(itemHolder: DslViewHolder) {
        itemHolder.click(R.id.item_rotate_view) {
            val renderer = itemRenderer
            if (renderer is BaseItemRenderer<*>) {
                itemHolder.context.canvasNumberWindow(it) {
                    val builder = StringBuilder()
                    onClickNumberAction = {
                        itemHolder.tv(R.id.item_rotate_view)?.text = updateValueWith(
                            builder,
                            itemHolder.tv(R.id.item_rotate_view)?.text?.toString(),
                            it,
                            1f
                        ).apply {
                            //this.toFloatOrNull()?.let(onDismiss)
                            //renderer.rotateBy()
                        }
                        false
                    }
                }
            }
        }
    }

    fun updateValueWith(
        newValueBuild: StringBuilder,
        firstValue: String?,
        op: String,
        step: Float
    ): String {
        when (op) {
            "-0" -> {
                //退格操作
                if (newValueBuild.isNotEmpty()) {
                    newValueBuild.deleteCharAt(newValueBuild.lastIndex)
                } else if (firstValue.isNullOrEmpty()) {
                    //no
                } else {
                    newValueBuild.append(firstValue.substring(0, firstValue.lastIndex))
                }
            }
            "-1" -> {
                //自减
                val oldValue = newValueBuild.toString()
                newValueBuild.clear()
                newValueBuild.append(oldValue.toFloatOrNull()?.run { "${this - step}" }
                    ?: "${-step}")
            }
            "+1" -> {
                //自增
                val oldValue = newValueBuild.toString()
                newValueBuild.clear()
                newValueBuild.append(oldValue.toFloatOrNull()?.run { "${this + step}" } ?: "$step")
            }
            "." -> {
                val value = newValueBuild.toString()
                if (value.contains(".")) {
                    //如果已经包含了点, 则忽略
                } else {
                    newValueBuild.append(op)
                }
            }
            else -> {
                newValueBuild.append(op)
            }
        }
        return newValueBuild.toString()
    }
}