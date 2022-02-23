package com.angcyo.uicore.component

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.NinePatch
import android.graphics.drawable.NinePatchDrawable
import java.nio.ByteBuffer
import java.nio.ByteOrder

class NinePatchBuilder {

    var width: Int
    var height: Int
    var bitmap: Bitmap? = null
    var resources: Resources? = null
    private val xRegions = ArrayList<Int>()
    private val yRegions = ArrayList<Int>()

    constructor(resources: Resources?, bitmap: Bitmap) {
        width = bitmap.width
        height = bitmap.height
        this.bitmap = bitmap
        this.resources = resources
    }

    constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun addXRegion(x: Int, width: Int): NinePatchBuilder {
        xRegions.add(x)
        xRegions.add(x + width)
        return this
    }

    fun addXRegionPoints(x1: Int, x2: Int): NinePatchBuilder {
        xRegions.add(x1)
        xRegions.add(x2)
        return this
    }

    fun addXRegion(xPercent: Float, widthPercent: Float): NinePatchBuilder {
        val xtmp = (xPercent * width).toInt()
        xRegions.add(xtmp)
        xRegions.add(xtmp + (widthPercent * width).toInt())
        return this
    }

    fun addXRegionPoints(x1Percent: Float, x2Percent: Float): NinePatchBuilder {
        xRegions.add((x1Percent * width).toInt())
        xRegions.add((x2Percent * width).toInt())
        return this
    }

    fun addXCenteredRegion(width: Int): NinePatchBuilder {
        val x = ((this.width - width) / 2)
        xRegions.add(x)
        xRegions.add(x + width)
        return this
    }

    fun addXCenteredRegion(widthPercent: Float): NinePatchBuilder {
        val width = (widthPercent * width).toInt()
        val x = ((this.width - width) / 2)
        xRegions.add(x)
        xRegions.add(x + width)
        return this
    }

    fun addYRegion(y: Int, height: Int): NinePatchBuilder {
        yRegions.add(y)
        yRegions.add(y + height)
        return this
    }

    fun addYRegionPoints(y1: Int, y2: Int): NinePatchBuilder {
        yRegions.add(y1)
        yRegions.add(y2)
        return this
    }

    fun addYRegion(yPercent: Float, heightPercent: Float): NinePatchBuilder {
        val ytmp = (yPercent * height).toInt()
        yRegions.add(ytmp)
        yRegions.add(ytmp + (heightPercent * height).toInt())
        return this
    }

    fun addYRegionPoints(y1Percent: Float, y2Percent: Float): NinePatchBuilder {
        yRegions.add((y1Percent * height).toInt())
        yRegions.add((y2Percent * height).toInt())
        return this
    }

    fun addYCenteredRegion(height: Int): NinePatchBuilder {
        val y = ((this.height - height) / 2)
        yRegions.add(y)
        yRegions.add(y + height)
        return this
    }

    fun addYCenteredRegion(heightPercent: Float): NinePatchBuilder {
        val height = (heightPercent * height).toInt()
        val y = ((this.height - height) / 2)
        yRegions.add(y)
        yRegions.add(y + height)
        return this
    }

    fun buildChunk(): ByteArray {
        if (xRegions.size == 0) {
            xRegions.add(0)
            xRegions.add(width)
        }
        if (yRegions.size == 0) {
            yRegions.add(0)
            yRegions.add(height)
        }
        /* example code from a anwser above
        // The 9 patch segment is not a solid color.
        private static final int NO_COLOR = 0x00000001;
        ByteBuffer buffer = ByteBuffer.allocate(56).order(ByteOrder.nativeOrder());
        //was translated
        buffer.put((byte)0x01);
        //divx size
        buffer.put((byte)0x02);
        //divy size
        buffer.put((byte)0x02);
        //color size
        buffer.put(( byte)0x02);

        //skip
        buffer.putInt(0);
        buffer.putInt(0);

        //padding
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);

        //skip 4 bytes
        buffer.putInt(0);

        buffer.putInt(left);
        buffer.putInt(right);
        buffer.putInt(top);
        buffer.putInt(bottom);
        buffer.putInt(NO_COLOR);
        buffer.putInt(NO_COLOR);

        return buffer;*/
        val NO_COLOR = 1 //0x00000001;
        val COLOR_SIZE = 9 //could change, may be 2 or 6 or 15 - but has no effect on output 
        val arraySize = 1 + 2 + 4 + 1 + xRegions.size + yRegions.size + COLOR_SIZE
        val byteBuffer = ByteBuffer.allocate(arraySize * 4).order(ByteOrder.nativeOrder())
        byteBuffer.put(1.toByte()) //was translated
        byteBuffer.put(xRegions.size.toByte()) //divisions x
        byteBuffer.put(yRegions.size.toByte()) //divisions y
        byteBuffer.put(COLOR_SIZE.toByte()) //color size

        //skip
        byteBuffer.putInt(0)
        byteBuffer.putInt(0)

        //padding -- always 0 -- left right top bottom
        byteBuffer.putInt(0)
        byteBuffer.putInt(0)
        byteBuffer.putInt(0)
        byteBuffer.putInt(0)

        //skip
        byteBuffer.putInt(0)
        for (rx in xRegions) byteBuffer.putInt(rx) // regions left right left right ...
        for (ry in yRegions) byteBuffer.putInt(ry) // regions top bottom top bottom ...
        for (i in 0 until COLOR_SIZE) byteBuffer.putInt(NO_COLOR)
        return byteBuffer.array()
    }

    fun buildNinePatch(): NinePatch? {
        val chunk = buildChunk()
        return if (bitmap != null) NinePatch(bitmap, chunk, null) else null
    }

    fun build(): NinePatchDrawable? {
        val ninePatch = buildNinePatch()
        return ninePatch?.let { NinePatchDrawable(resources, it) }
    }
}