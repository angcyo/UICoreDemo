package com.angcyo.uicore.demo

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.angcyo.drawable.dpi
import com.angcyo.drawable.getDrawable
import com.angcyo.library.ex.randomColorAlpha
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppImageItem
import com.angcyo.uicore.dslitem.image
import com.angcyo.widget.recycler.resetLayoutManager

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/20
 */

class ImageDemo : AppDslFragment() {

    override fun onInitDslLayout() {
        super.onInitDslLayout()

        _vh.rv(R.id.lib_recycler_view)?.resetLayoutManager("SV2")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        renderDslAdapter {
            for (i in 0..125) {
                AppImageItem()() {
                    imageUrl = image()
                    imageHeight = if (i % 2 == 0) 280 * dpi else 240 * dpi

                    onConfigGlideImage = {
                        it.placeholderDrawable = ColorDrawable(randomColorAlpha())
                        it.errorDrawable = ColorDrawable(randomColorAlpha())

                        it.maskDrawable = when {
                            i % 5 == 0 -> getDrawable(R.drawable.ic_logo)
                            i % 4 == 0 -> getDrawable(R.drawable.qipao_guosheng)
                            i % 3 == 0 -> getDrawable(R.drawable.qipao1)
                            i % 2 == 0 -> getDrawable(R.drawable.qipao)
                            else -> null
                        }
                    }
                }
            }
        }
    }
}