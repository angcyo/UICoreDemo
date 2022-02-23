package com.angcyo.uicore.component

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.NinePatch
import android.graphics.Rect
import android.graphics.drawable.NinePatchDrawable
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * User: bgriffey
 * Date: 12/27/12
 * Time: 2:37 PM
 */
object NinePatchBitmapFactory {

    // The 9 patch segment is not a solid color.
    private const val NO_COLOR = 0x00000001

    // The 9 patch segment is completely transparent.
    private const val TRANSPARENT_COLOR = 0x00000000

    fun createNinePathWithCapInsets(
        res: Resources?,
        bitmap: Bitmap?,
        top: Int,
        left: Int,
        bottom: Int,
        right: Int,
        srcName: String?
    ): NinePatchDrawable {
        val buffer = getByteBuffer(top, left, bottom, right)
        return NinePatchDrawable(res, bitmap, buffer.array(), Rect(), srcName)
    }

    fun createNinePatch(
        bitmap: Bitmap?,
        top: Int,
        left: Int,
        bottom: Int,
        right: Int,
        srcName: String?
    ): NinePatch {
        val buffer = getByteBuffer(top, left, bottom, right)
        return NinePatch(bitmap, buffer.array(), srcName)
    }

    private fun getByteBuffer(top: Int, left: Int, bottom: Int, right: Int): ByteBuffer {
        //Docs check the NinePatchChunkFile
        val buffer = ByteBuffer.allocate(56).order(ByteOrder.nativeOrder())
        //was translated
        buffer.put(0x01.toByte())
        //divx size
        buffer.put(0x02.toByte())
        //divy size
        buffer.put(0x02.toByte())
        //color size
        buffer.put(0x02.toByte())

        //skip
        buffer.putInt(0)
        buffer.putInt(0)

        //padding
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)

        //skip 4 bytes
        buffer.putInt(0)
        buffer.putInt(left)
        buffer.putInt(right)
        buffer.putInt(top)
        buffer.putInt(bottom)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        return buffer
    }
}