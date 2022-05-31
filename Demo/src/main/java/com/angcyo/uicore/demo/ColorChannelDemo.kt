package com.angcyo.uicore.demo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import com.angcyo.canvas.utils.createPaint
import com.angcyo.component.getPhoto
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.colorChannel
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.canvas.loadingAsync
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/31
 */
class ColorChannelDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_color_channel_layout) { itemHolder, itemPosition, adapterItem, payloads ->

                itemHolder.click(R.id.select_button) {
                    fContext().getPhoto(parentFragmentManager) {
                        itemHolder.img(R.id.image_view)?.setImageBitmap(it)
                    }
                }

                itemHolder.click(R.id.red_channel_button) {
                    extractChannel(Color.RED, itemHolder)
                    extractChannel(Color.GREEN, itemHolder)
                    extractChannel(Color.BLUE, itemHolder)
                }
                itemHolder.click(R.id.green_channel_button) {
                    extractChannel(Color.GREEN, itemHolder)
                    extractChannel(Color.RED, itemHolder)
                    extractChannel(Color.BLUE, itemHolder)
                }
                itemHolder.click(R.id.blue_channel_button) {
                    extractChannel(Color.BLUE, itemHolder)
                    extractChannel(Color.RED, itemHolder)
                    extractChannel(Color.GREEN, itemHolder)
                }
                itemHolder.click(R.id.alpha_channel_button) {
                    extractChannel(Color.TRANSPARENT, itemHolder)
                }
            }
        }
    }

    fun extractChannel(channel: Int, itemHolder: DslViewHolder) {
        itemHolder.img(R.id.image_view)?.drawable?.let {
            if (it is BitmapDrawable) {
                loadingAsync({
                    val width = it.bitmap.width
                    val height = it.bitmap.height
                    val bytes = it.bitmap.colorChannel(channel)

                    val channelBitmap =
                        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(channelBitmap)
                    val paint = createPaint(
                        if (channel == Color.TRANSPARENT) Color.BLACK else channel,
                        Paint.Style.FILL
                    )
                    for (y in 0 until height) {
                        for (x in 0 until width) {
                            val value: Int = bytes[y * width + x].toInt()

                            paint.color = Color.argb(
                                if (channel == Color.TRANSPARENT) value else 0,
                                if (channel == Color.RED) value else 0,
                                if (channel == Color.GREEN) value else 0,
                                if (channel == Color.BLUE) value else 0
                            )
                            canvas.drawCircle(x.toFloat(), y.toFloat(), 1f, paint)

                            if (value <= 240) {
                                //有值
                            } else {
                                //忽略值
                            }
                        }
                    }
                    channelBitmap
                }) { channelBitmap ->
                    itemHolder.img(
                        when (channel) {
                            Color.RED -> R.id.red_image_view
                            Color.BLUE -> R.id.blue_image_view
                            Color.GREEN -> R.id.green_image_view
                            else -> R.id.alpha_image_view
                        }
                    )?.setImageBitmap(channelBitmap)
                }
            }
        }
    }

}