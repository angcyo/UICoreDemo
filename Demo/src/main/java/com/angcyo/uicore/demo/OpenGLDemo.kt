package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import com.angcyo.library.ex.stackOf
import com.angcyo.opengl.core.BaseOpenGLRenderer
import com.angcyo.opengl.core.IOpenGLSurface
import com.angcyo.opengl.core.Vector3
import com.angcyo.opengl.primitives.OpenGLGCodeLine
import com.angcyo.opengl.primitives.OpenGLGCodeLineData
import com.angcyo.opengl.primitives.OpenGLLine
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.seek

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @date 2025/03/17
 */
class OpenGLDemo : AppTitleFragment() {

    init {
        contentLayoutId = R.layout.fragment_opengl
    }

    /**线条*/
    var openglGCodeLine: OpenGLGCodeLine? = null

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        val seekBar = _vh.seek(R.id.seek_bar) { value, fraction, fromUser ->
            openglGCodeLine?.renderProgress = fraction
        }

        val openGlSurface: IOpenGLSurface? = _vh.view(R.id.opengl_view) as IOpenGLSurface?
        openGlSurface?.setSurfaceRenderer(object : BaseOpenGLRenderer(fContext()) {
            override fun initScene() {
                //getCurrentScene().addChild(TestOpenGLObject())
                getCurrentScene().addChild(
                    OpenGLLine(
                        stackOf(
                            Vector3(-0.5f, 0.5f, 0f),
                            Vector3(0f, 0.5f, 0f),
                            Vector3(0f, -0.5f, 0f),
                            Vector3(0.5f, -0.5f, 0f),
                        ),
                        intArrayOf(
                            Color.RED,
                            Color.GREEN,
                            Color.BLUE,
                            Color.YELLOW,
                        ),
                    )
                )
                getCurrentScene().addChild(
                    OpenGLGCodeLine(
                        stackOf(
                            OpenGLGCodeLineData(
                                startPoint = PointF(1f, 1f),
                                endPoint = PointF(2f, 1f),
                                color = Color.RED,
                            ),
                            OpenGLGCodeLineData(
                                startPoint = PointF(2f, 1f),
                                endPoint = PointF(2f, 2f),
                                color = Color.GREEN,
                            ),
                            OpenGLGCodeLineData(
                                startPoint = PointF(2f, 2f),
                                endPoint = PointF(3f, 2f),
                                color = Color.BLUE,
                            ),
                        )
                    ).apply {
                        //getModelMatrix()
                        openglGCodeLine = this
                    }
                )

                //--
                //getCurrentScene().scaleSceneBy(0.25f, 0.25f)
                //getCurrentScene().translateSceneBy(0f, 0f,0f)
                getCurrentScene().testMatrix()
            }
        })
    }

}