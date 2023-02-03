package com.angcyo.uicore.demo.draw

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/01
 */
interface IMatrixView {

    //---旋转的度数, 角度单位

    var groupRotate: Float

    var subRotate: Float

    //---缩放比例值

    var groupScaleX: Float
    var groupScaleY: Float

    var subScaleX: Float
    var subScaleY: Float

    //---缩放后的, 倾斜度数, 角度单位

    var subSkewX: Float
    var subSkewY: Float
}