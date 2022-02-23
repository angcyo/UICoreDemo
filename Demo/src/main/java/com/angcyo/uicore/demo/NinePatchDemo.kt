package com.angcyo.uicore.demo

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.NinePatchBitmapFactory2
import com.angcyo.uicore.component.NinePatchBuilder
import java.io.BufferedInputStream
import java.io.IOException


/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class NinePatchDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            dslItem(R.layout.nine_patch_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->

                    /*--------1------*/

                    itemHolder.view(R.id.image_view)?.background =
                        loadDrawableFromAsset("chat_bubble_left_9.9.png", fContext())
                    /*NinePatchBitmapFactory.createNinePathWithCapInsets(
                        resources,
                        _drawable(R.mipmap.chat_bubble_left)?.toBitmap(),
                        1,
                        1,
                        1,
                        1,
                        "name1"
                    )*/
                    itemHolder.view(R.id.image_horizontal_view)?.background =
                        loadDrawableFromAsset("chat_bubble_left_9.9.png", fContext())

                    itemHolder.view(R.id.image_vertical_view)?.background =
                        loadDrawableFromAsset("chat_bubble_left_9.9.png", fContext())

                    itemHolder.view(R.id.image_view2)?.background =
                        loadDrawableFromAsset("chat_bubble_left_9.9.png", fContext())


                    /*--------2------*/

                    itemHolder.view(R.id.image2_view)?.background =
                        NinePatchBitmapFactory2.createNinePatchDrawable(
                            resources,
                            loadBitmapAsset("chat_bubble_left_9.9.png", fContext())!!
                        )

                    itemHolder.view(R.id.image2_horizontal_view)?.background =
                        NinePatchBitmapFactory2.createNinePatchDrawable(
                            resources,
                            loadBitmapAsset("chat_bubble_left_9.9.png", fContext())!!
                        )

                    itemHolder.view(R.id.image2_vertical_view)?.background =
                        NinePatchBitmapFactory2.createNinePatchDrawable(
                            resources,
                            loadBitmapAsset("chat_bubble_left_9.9.png", fContext())!!
                        )

                    itemHolder.view(R.id.image2_view2)?.background =
                        NinePatchBitmapFactory2.createNinePatchDrawable(
                            resources,
                            loadBitmapAsset("chat_bubble_left_9.9.png", fContext())!!
                        )

                    /*val bitmap = _drawable(R.drawable.chat_bubble_left_9)?.toBitmap()
                    val ninePatch = NinePatch.isNinePatchChunk(bitmap!!.ninePatchChunk)
                    if (ninePatch) {
                        itemHolder.view(R.id.image_view3)?.background =
                            NinePatchDrawable(
                                resources,
                                bitmap,
                                bitmap.ninePatchChunk,
                                null,
                                "name2"
                            )
                    }*/

                    /*NinePatchBitmapFactory.createNinePathWithCapInsets(
                        resources,
                        _drawable(R.mipmap.chat_bubble_left)?.toBitmap(),
                        30,
                        15,
                        30,
                        15,
                        "name2"
                    )*/

                    /*--------3------*/

                    //or add multiple patches
                    val builder = NinePatchBuilder(
                        resources,
                        loadBitmapAsset("chat_bubble_left.png", fContext())!!
                    )
                    builder.addXRegion(30, 2).addXRegion(50, 1).addYRegion(20, 4)
                    val chunk = builder.buildChunk()
                    val ninepatch = builder.buildNinePatch()
                    val drawable = builder.build()

                    itemHolder.view(R.id.image3_view)?.background = drawable
                    itemHolder.view(R.id.image3_horizontal_view)?.background = drawable
                    itemHolder.view(R.id.image3_vertical_view)?.background = drawable
                    itemHolder.view(R.id.image3_view2)?.background = drawable
                }
            }
        }
    }

    fun loadBitmapAsset(fileName: String?, context: Context): Bitmap? {
        val assetManager: AssetManager = context.assets
        var bis: BufferedInputStream? = null
        try {
            bis = BufferedInputStream(assetManager.open(fileName!!))
            return BitmapFactory.decodeStream(bis)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bis?.close()
            } catch (e: Exception) {
            }
        }
        return null
    }

    /*fun loadBitmapDrawable(fileName: String?, context: Context): Bitmap? {
        *//*val input = resources.openRawResource(R.raw.chat_bubble_left_9)
        NinePatchDrawable.createFromStream(input, null)*//*
    }*/

    fun loadDrawableFromAsset(fileName: String?, context: Context): Drawable? {
        val assetManager: AssetManager = context.assets
        return NinePatchDrawable.createFromStream(assetManager.open(fileName!!), null)
    }

}