package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.opengl.BaseOpenGLRenderer
import com.angcyo.opengl.IOpenGLSurface
import com.angcyo.opengl.TestOpenGLObject
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
                getCurrentScene().addChild(TestOpenGLObject())
            }
        })
    }

}