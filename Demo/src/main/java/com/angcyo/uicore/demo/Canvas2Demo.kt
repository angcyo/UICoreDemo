package com.angcyo.uicore.demo

import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.CanvasRenderView
import com.angcyo.canvas2.laser.pecker.RenderLayoutHelper
import com.angcyo.canvas2.laser.pecker.renderDelegate
import com.angcyo.canvas2.laser.pecker.util.restoreProjectState
import com.angcyo.canvas2.laser.pecker.util.saveProjectState
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave.EngraveFlowLayoutHelper
import com.angcyo.engrave.IEngraveCanvasFragment
import com.angcyo.engrave.data.HawkEngraveKeys
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.library.unit.MmValueUnit
import com.angcyo.uicore.MainFragment
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023-2-11
 */
class Canvas2Demo : AppDslFragment(), IEngraveCanvasFragment {

    val renderLayoutHelper = RenderLayoutHelper(this)

    init {
        enableSoftInput = false
    }

    override fun canSwipeBack(): Boolean = false

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.canvas2_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_view)

                //1.
                renderLayoutHelper.bindCanvasLayout(itemHolder)

                itemHolder.click(R.id.canvas_view) {
                    canvasRenderView?.invalidate()
                }

                //testCanvasRenderView(itemHolder)
                testDemo(itemHolder)
            }
        }
    }

    fun testCanvasRenderView(itemHolder: DslViewHolder) {
        val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_view)
        canvasRenderView?.delegate?.apply {
            renderViewBox.originGravity = Gravity.CENTER
        }
    }

    fun testDemo(itemHolder: DslViewHolder) {
        itemHolder.click(R.id.preview_button) {
            _adapter.render {
                PreviewBitmapItem()() {
                    itemHolder.renderDelegate?.let {
                        bitmap = it.preview()
                    }
                }
            }
        }
        itemHolder.click(R.id.preview_rect_button) {
            _adapter.render {
                PreviewBitmapItem()() {
                    itemHolder.renderDelegate?.let {
                        bitmap = it.preview(overrideSize = HawkEngraveKeys.projectOutSize.toFloat())
                    }
                }
            }
        }
        itemHolder.click(R.id.bounds_button) {
            itemHolder.renderDelegate?.apply {
                val unit = MmValueUnit()
                val limitRect = when {
                    MainFragment.CLICK_COUNT++ % 3 == 2 -> RectF(
                        unit.convertValueToPixel(100f),
                        unit.convertValueToPixel(100f),
                        unit.convertValueToPixel(300f),
                        unit.convertValueToPixel(300f)
                    )
                    MainFragment.CLICK_COUNT++ % 2 == 0 -> RectF(
                        unit.convertValueToPixel(-30f),
                        unit.convertValueToPixel(-30f),
                        unit.convertValueToPixel(-10f),
                        unit.convertValueToPixel(-10f)
                    )
                    else -> RectF(
                        unit.convertValueToPixel(10f),
                        unit.convertValueToPixel(10f),
                        unit.convertValueToPixel(30f),
                        unit.convertValueToPixel(30f)
                    )
                }
                showRectBounds(limitRect)
            }
        }
    }

    //region---core---

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        //restore
        _vh.postDelay(0) {
            renderLayoutHelper.canvasRenderDelegate?.restoreProjectState()
        }
    }

    override fun onDestroyView() {
        //save
        renderLayoutHelper.canvasRenderDelegate?.saveProjectState()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        //engraveFlowLayoutHelper.loopCheckDeviceState = false
        //GraphicsHelper.restoreLocation()
    }

    //endregion---core---

    //<editor-fold desc="IEngraveCanvasFragment">

    override val fragment: AbsLifecycleFragment
        get() = this

    override val engraveFlowLayoutHelper: EngraveFlowLayoutHelper
        get() = EngraveFlowLayoutHelper()

    override val canvasDelegate: CanvasDelegate?
        get() = null

    override val flowLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.engrave_flow_wrap_layout)

    //</editor-fold desc="IEngraveCanvasFragment">
}