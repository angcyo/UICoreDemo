package com.angcyo.uicore.demo

import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.angcyo.base.dslFHelper
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.CanvasRenderView
import com.angcyo.canvas2.laser.pecker.RenderLayoutHelper
import com.angcyo.canvas2.laser.pecker.renderDelegate
import com.angcyo.canvas2.laser.pecker.util.restoreProjectState
import com.angcyo.canvas2.laser.pecker.util.saveProjectState
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave.BaseFlowLayoutHelper
import com.angcyo.engrave.EngraveFlowLayoutHelper
import com.angcyo.engrave.IEngraveCanvasFragment
import com.angcyo.engrave.data.HawkEngraveKeys
import com.angcyo.engrave.isEngraveFlow
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.item.component.DebugFragment
import com.angcyo.library.component.MultiFingeredHelper
import com.angcyo.library.component.pad.isInPadMode
import com.angcyo.library.unit.MmValueUnit
import com.angcyo.uicore.MainFragment
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023-2-11
 */
class Canvas2Demo : AppDslFragment(), IEngraveCanvasFragment {

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
                renderLayoutHelper.bindRenderLayout(itemHolder)

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

    //<editor-fold desc="touch">

    val pinchGestureDetector = MultiFingeredHelper.PinchGestureDetector().apply {
        onPinchAction = {
            dslFHelper {
                show(DebugFragment::class)
            }
        }
    }

    override fun onDispatchTouchEvent(event: MotionEvent) {
        super.onDispatchTouchEvent(event)
        pinchGestureDetector.onTouchEvent(event)
    }

    //</editor-fold desc="touch">

    //<editor-fold desc="init">

    /**Canvas2布局*/
    val renderLayoutHelper = RenderLayoutHelper(this)

    /**雕刻布局*/
    val _engraveFlowLayoutHelper = EngraveFlowLayoutHelper().apply {
        backPressedDispatcherOwner = this@Canvas2Demo

        onEngraveFlowChangedAction = { from, to ->
            //禁止手势
            val isEngraveFlow = to.isEngraveFlow()
            renderLayoutHelper.canvasRenderDelegate?.disableEditTouchGesture(isEngraveFlow)
            if (isInPadMode()) {
                renderLayoutHelper.disableEditItem(isEngraveFlow)
            }

            if (to == BaseFlowLayoutHelper.ENGRAVE_FLOW_PREVIEW) {
                //预览中, 偏移画板界面
                val productInfoData = laserPeckerModel.productInfoData
                productInfoData.value?.bounds?.let {
                    renderLayoutHelper.canvasRenderDelegate?.showRectBounds(
                        it,
                        offsetRectTop = true
                    )
                }
            } else if (to == BaseFlowLayoutHelper.ENGRAVE_FLOW_BEFORE_CONFIG) {
                renderLayoutHelper.canvasRenderDelegate?.saveProjectState()
            }
        }

        onIViewEvent = { iView, event ->
            val start = event == Lifecycle.Event.ON_START
            val destroy = event == Lifecycle.Event.ON_DESTROY

            if (destroy) {
                _vh.enable(R.id.lib_title_wrap_layout, null)
                _vh.enable(R.id.device_tip_wrap_layout, null)
            } else {
                _vh.enable(R.id.lib_title_wrap_layout, destroy)
                _vh.enable(R.id.device_tip_wrap_layout, destroy)
            }
        }

        onEngraveParamsChangeAction = {
            renderLayoutHelper.updateLayerListLayout()
        }
    }

    //</editor-fold desc="init">

    //<editor-fold desc="IEngraveCanvasFragment">

    override val fragment: AbsLifecycleFragment
        get() = this

    override val engraveFlowLayoutHelper: EngraveFlowLayoutHelper
        get() = _engraveFlowLayoutHelper.apply {
            engraveCanvasFragment = this@Canvas2Demo
        }

    override val canvasDelegate: CanvasDelegate?
        get() = null

    override val flowLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.engrave_flow_wrap_layout)

    //</editor-fold desc="IEngraveCanvasFragment">
}