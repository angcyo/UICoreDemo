package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.component.toast
import com.angcyo.drawable.dp
import com.angcyo.drawable.dpi
import com.angcyo.drawable.getDrawable
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.base.addTo

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/18
 */
class ViewGroupOverlayDemo : AppTitleFragment() {
    init {
        contentLayoutId = R.layout.demo_view_group_overlay
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _vh.click(R.id.button) { button ->
            _vh.group(R.id.linear_layout)?.overlay?.apply {
                //image
                add(_vh.view(R.id.image_view)!!.apply {
                    animate().translationXBy(100 * dp)
                        .translationYBy(60 * dp)
                        .setDuration(3000)
                        .start()
                })
                //add(_vh.view(R.id.image_view2)!!)

                //drawable
                add(getDrawable(R.drawable.ic_logo_small)!!.apply {
                    _vh.postDelay(200) {
                        setBounds(80 * dpi, 80 * dpi, 120 * dpi, 120 * dpi)
                    }
                })

                //button
                add(button.apply {
                    animate().translationXBy(100 * dp)
                        .translationYBy(60 * dp)
                        .setDuration(3000)
                        .start()
                })
            }
        }

        _vh.click(R.id.button2) {
            val button1 = _vh.view(R.id.button)
            button1?.addTo(_vh.group(R.id.frame_layout)) {
                it.translationX = 0f
                it.translationY = 0f
            }
            toast("button1...$button1")
        }
    }
}