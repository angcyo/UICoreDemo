package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.library.ex.stackOf
import com.angcyo.opengl.core.BaseOpenGLRenderer
import com.angcyo.opengl.core.IOpenGLSurface
import com.angcyo.opengl.core.Vector3
import com.angcyo.opengl.primitives.OpenGLLine
import com.angcyo.uicore.base.AppTitleFragment

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @date 2025/03/17
 */
class OpenGLDemo : AppTitleFragment() {

    init {
        contentLayoutId = R.layout.fragment_opengl
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        val openGlSurface: IOpenGLSurface? = _vh.view(R.id.opengl_view) as IOpenGLSurface?
        openGlSurface?.setSurfaceRenderer(object : BaseOpenGLRenderer(fContext()) {
            override fun initScene() {
                //getCurrentScene().addChild(TestOpenGLObject())
                val points = stackOf(
                    Vector3(-0.5f, 0.5f, 0f),
                    Vector3(0f, 0.5f, 0f),
                    Vector3(0f, -0.5f, 0f),
                    Vector3(0.5f, -0.5f, 0f),
                )
                val colors = intArrayOf(
                    Color.RED,
                    Color.GREEN,
                    Color.BLUE,
                    Color.YELLOW,
                )
                getCurrentScene().addChild(OpenGLLine(points, colors))
            }
        })
    }

}