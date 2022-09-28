package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import com.angcyo.canvas.utils.parseGCode
import com.angcyo.drawable.CheckerboardDrawable
import com.angcyo.dsladapter.bindItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.readAssets
import com.angcyo.library.ex.readResource
import com.angcyo.library.ex.setBgDrawable
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.base.string
import com.pixplicity.sharp.Sharp

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/29
 */
class SvgDemo : AppDslFragment() {

    companion object {

        //爱心 空心
        const val LOVE_HOLLOW =
            "<svg t=\"1648448062035\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"2391\" width=\"200\" height=\"200\">\n" +
                    "\n" +
                    "<path d=\"M502.538 793.068L810.045 485.58a176.476 176.476 0 1 0-249.569-249.57l-57.938 57.918-57.917-57.917A176.476 176.476 0 0 0 195.05 485.58l307.487 307.487z m28.98 86.896a40.96 40.96 0 0 1-57.938 0L137.114 543.498C36.209 442.593 36.209 279 137.114 178.074c100.925-100.905 264.52-100.905 365.424 0 100.926-100.905 264.52-100.905 365.425 0 100.925 100.925 100.925 264.52 0 365.424L531.517 879.985z\" p-id=\"2392\">\n" +
                    "</path>\n" +
                    "\n" +
                    "</svg>"

        //爱心 空心 颜色
        const val LOVE_HOLLOW_COLOR =
            "<svg t=\"1648455538406\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"8421\" width=\"200\" height=\"200\"><path d=\"M502.538 793.068L810.045 485.58a176.476 176.476 0 1 0-249.569-249.57l-57.938 57.918-57.917-57.917A176.476 176.476 0 0 0 195.05 485.58l307.487 307.487z m28.98 86.896a40.96 40.96 0 0 1-57.938 0L137.114 543.498C36.209 442.593 36.209 279 137.114 178.074c100.925-100.905 264.52-100.905 365.424 0 100.926-100.905 264.52-100.905 365.425 0 100.925 100.925 100.925 264.52 0 365.424L531.517 879.985z\" p-id=\"8422\" fill=\"#d4237a\"></path></svg>\n"

        //爱心 实心
        const val LOVE_SOLID =
            "<svg t=\"1648448103260\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"2526\" width=\"200\" height=\"200\">\n" +
                    "\n" +
                    "<path d=\"M917.333333 166.4c-106.666667-106.666667-285.866667-106.666667-392.533333 0l-12.8 17.066667-17.066667-12.8C388.266667 64 209.066667 64 102.4 170.666667s-106.666667 285.866667 0 392.533333l375.466667 375.466667c17.066667 17.066667 42.666667 17.066667 59.733333 0l375.466667-375.466667c110.933333-110.933333 110.933333-290.133333 4.266666-396.8z\" p-id=\"2527\">\n" +
                    "</path>\n" +
                    "\n" +
                    "</svg>"

        //爱心 实心 颜色
        const val LOVE_SOLID_COLOR =
            "<svg t=\"1648455552114\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"8591\" width=\"200\" height=\"200\"><path d=\"M917.333333 166.4c-106.666667-106.666667-285.866667-106.666667-392.533333 0l-12.8 17.066667-17.066667-12.8C388.266667 64 209.066667 64 102.4 170.666667s-106.666667 285.866667 0 392.533333l375.466667 375.466667c17.066667 17.066667 42.666667 17.066667 59.733333 0l375.466667-375.466667c110.933333-110.933333 110.933333-290.133333 4.266666-396.8z\" p-id=\"8592\" fill=\"#d4237a\"></path></svg>\n"

        const val testGCode = "G1 X0 Y0\n" +
                "\n" +
                "1.6004 Y17.2065 I45.0088 J0.\n" +
                "G2 X-31.8437 Y31.8084 I41.5827 J-17.2241\n" +
                "G2 X-17.2418 Y41.5651 I31.826 J-31.826\n" +
                "G2 X-0.0176 Y44.9912 I17.2241 J-41.5827\n" +
                "G2 X31.8084 Y31.8084 I-0. J-45.0088\n" +
                "G2 X44.9912 Y-0.0176 I-31.826 J-31.826\n" +
                "G1  X44.9912 Y-0.0176"

        val svgResList = mutableListOf<Int>().apply {
            add(R.raw.android) //机器人
            add(R.raw.cartman) //卡特曼, 卡通人物
            add(R.raw.cartman2) //卡特曼, 卡通人物
            add(R.raw.emotion) //bored
            add(R.raw.group_transparency) //4个圆,一根线
            add(R.raw.issue_19) //房子house
            add(R.raw.mother) //mommys
            add(R.raw.quadratic_bezier) //三阶贝塞尔
        }

        val gCodeNameList = mutableListOf<String>().apply {
            add("gcode/issue_19.gcode")
            add("gcode/zhou.gcode")
            add("gcode/ke.gcode")
            add("gcode/concentric_circle.gcode")
            add("gcode/square_circle.gcode")
            add("gcode/grid.gcode")
            add("gcode/cherryblossoms.gcode")
            add("gcode/LaserPecker.gcode")
            add("gcode/snowflakes.gcode")
            add("gcode/love.gcode")
        }
    }

    init {
        Sharp.setLogLevel(Sharp.LOG_LEVEL_INFO)
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_svg) { itemHolder, itemPosition, adapterItem, payloads ->
                val editText = itemHolder.ev(R.id.edit_text_view)
                val imageView = itemHolder.img(R.id.image_view)
                imageView?.background = CheckerboardDrawable.create()

                //click

                itemHolder.click(R.id.parse_button) {
                    val text = editText.string()
                    imageView?.apply {
                        setBgDrawable(CheckerboardDrawable.create())
                        Sharp.loadString(text).into(this)
                    }
                }

                itemHolder.click(R.id.parse_gcode_button) {
                    val text = editText.string()
                    //Sharp.loadString(text).into(imageView!!)
                    imageView?.apply {
                        setBackgroundColor(Color.WHITE)
                        setImageDrawable(GCodeHelper.parseGCode(text))
                    }
                }

                //scale

                itemHolder.click(R.id.fit_center_button) {
                    imageView?.scaleType = ImageView.ScaleType.FIT_CENTER
                }

                itemHolder.click(R.id.center_inside_button) {
                    imageView?.scaleType = ImageView.ScaleType.CENTER_INSIDE
                }

                itemHolder.click(R.id.love_hollow) {
                    editText?.setInputText(LOVE_HOLLOW)
                }
                itemHolder.click(R.id.love_hollow_color) {
                    editText?.setInputText(LOVE_HOLLOW_COLOR)
                }
                itemHolder.click(R.id.love_solid) {
                    editText?.setInputText(LOVE_SOLID)
                }
                itemHolder.click(R.id.love_solid_color) {
                    editText?.setInputText(LOVE_SOLID_COLOR)
                }

                //svg
                itemHolder.click(R.id.android_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.android))
                }
                itemHolder.click(R.id.cartman_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.cartman))
                    //Sharp.loadResource(resources, R.raw.cartman).into(imageView!!)
                    /*Sharp.loadResource(resources, R.raw.cartman).getSharpPicture {
                        SharpDrawable.prepareView(imageView)
                        imageView?.setImageDrawable(it.drawable)
                    }*/
                }
                itemHolder.click(R.id.cartman2_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.cartman2))
                }
                itemHolder.click(R.id.emotion_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.emotion))
                }
                itemHolder.click(R.id.group_transparency_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.group_transparency))
                }
                itemHolder.click(R.id.issue_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.issue_19))
                }
                itemHolder.click(R.id.layout_item_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.layout_item))
                }
                itemHolder.click(R.id.mother_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.mother))
                }
                itemHolder.click(R.id.quadratic_svg) {
                    editText?.setInputText(fContext().readResource(R.raw.quadratic_bezier))
                }

                //gcode
                itemHolder.click(R.id.load_issue_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/issue_19.gcode"))
                }
                itemHolder.click(R.id.load_zhou_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/zhou.gcode"))
                }
                itemHolder.click(R.id.load_ke_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/ke.gcode"))
                }
                itemHolder.click(R.id.concentric_circle_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/concentric_circle.gcode"))
                }
                itemHolder.click(R.id.square_circle_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/square_circle.gcode"))
                }
                itemHolder.click(R.id.grid_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/grid.gcode"))
                }
                itemHolder.click(R.id.cherryblossoms_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/cherryblossoms.gcode"))
                }
                itemHolder.click(R.id.LaserPecker_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/LaserPecker.gcode"))
                }
                itemHolder.click(R.id.snowflakes_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/snowflakes.gcode"))
                }
                itemHolder.click(R.id.love_gcode) {
                    editText?.setInputText(fContext().readAssets("gcode/love.gcode"))
                }

                //test
                editText?.setInputText(testGCode)
            }
        }
    }

}