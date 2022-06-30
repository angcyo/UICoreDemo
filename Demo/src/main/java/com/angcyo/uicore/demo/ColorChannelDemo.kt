package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.TextView
import com.angcyo.canvas.utils.toEngraveBitmap
import com.angcyo.component.getPhoto
import com.angcyo.drawable.PathDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave.canvas.loadingAsync
import com.angcyo.library.ex.*
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.clickIt

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/31
 */
class ColorChannelDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_color_channel_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.img(R.id.image_view)?.setImageResource(R.drawable.face)

                itemHolder.click(R.id.select_button) {
                    fContext().getPhoto(parentFragmentManager) {
                        itemHolder.img(R.id.image_view)?.setImageBitmap(it)
                    }
                }

                itemHolder.click(R.id.red_channel_button) {
                    extractChannel(Color.RED, itemHolder)
                    extractChannel(Color.GREEN, itemHolder)
                    extractChannel(Color.BLUE, itemHolder)
                    extractEngraveChannel(itemHolder)
                }
                itemHolder.click(R.id.green_channel_button) {
                    extractChannel(Color.GREEN, itemHolder)
                    extractChannel(Color.RED, itemHolder)
                    extractChannel(Color.BLUE, itemHolder)
                    extractEngraveChannel(itemHolder)
                }
                itemHolder.click(R.id.blue_channel_button) {
                    extractChannel(Color.BLUE, itemHolder)
                    extractChannel(Color.RED, itemHolder)
                    extractChannel(Color.GREEN, itemHolder)
                    extractEngraveChannel(itemHolder)
                }
                itemHolder.click(R.id.alpha_channel_button) {
                    extractChannel(Color.TRANSPARENT, itemHolder)
                    extractEngraveChannel(itemHolder)
                }
                itemHolder.click(R.id.engrave_channel_button) {
                    extractEngraveChannel(itemHolder)
                }
                itemHolder.click(R.id.gray_button) {
                    grayHandle(itemHolder)
                }

                //text test
                val textView1 = itemHolder.tv(R.id.lp_text_view1)?.apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    clickIt {
                        testPathDrawable(itemHolder, this)
                    }
                }
                val textView2 = itemHolder.tv(R.id.lp_text_view2)?.apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                    clickIt {
                        testPathDrawable(itemHolder, this)
                    }
                }
                val textView3 = itemHolder.tv(R.id.lp_text_view3)?.apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    clickIt {
                        testPathDrawable(itemHolder, this)
                    }
                }
                val textView4 = itemHolder.tv(R.id.lp_text_view4)?.apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
                    clickIt {
                        testPathDrawable(itemHolder, this)
                    }
                }
                val resultTextView = itemHolder.tv(R.id.lp_result_view)
                itemHolder.click(R.id.get_bounds_button) {
                    resultTextView?.text = buildString {

                        fun appendView(tv: TextView?) {
                            val bounds = tv?.paint?.textBounds(tv.text)
                            append(bounds)
                            append(" w:${bounds?.width()} h:${bounds?.height()}")
                            appendLine()
                        }

                        appendView(textView1)
                        appendView(textView2)
                        appendView(textView3)
                        appendView(textView4)
                    }
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

                    val channelBitmap = bytes.toChannelBitmap(width, height, channel)
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

    /**红色通道*/
    fun extractEngraveChannel(itemHolder: DslViewHolder) {
        itemHolder.img(R.id.image_view)?.drawable?.let {
            if (it is BitmapDrawable) {
                loadingAsync({
                    val width = it.bitmap.width
                    val height = it.bitmap.height
                    val bytes = it.bitmap.colorChannel(Color.RED)

                    val bitmap = bytes.toEngraveBitmap(width, height)
                    bitmap
                }) { bitmap ->
                    itemHolder.img(R.id.engrave_image_view)?.setImageBitmap(bitmap)
                }
            }
        }
    }

    /**灰度*/
    fun grayHandle(itemHolder: DslViewHolder) {
        itemHolder.img(R.id.image_view)?.drawable?.let {
            if (it is BitmapDrawable) {
                loadingAsync({
                    it.bitmap.grayHandle { color, alpha ->
                        0xFF
                    }
                }) { bitmap ->
                    itemHolder.img(R.id.blue_image_view)?.setImageBitmap(bitmap)

                    itemHolder.click(R.id.blue_image_view) {
                        itemHolder.img(R.id.image_view)?.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    fun testPathDrawable(itemHolder: DslViewHolder, tv: TextView) {
        itemHolder.img(R.id.alpha_image_view)?.apply {
            setImageDrawable(PathDrawable().apply {
                setPath(tv.paint.textPath(tv.text.toString()))
            })
        }
    }

}