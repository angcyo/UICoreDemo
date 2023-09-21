package com.angcyo.uicore.test

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/09/21
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION
)
annotation class TestAnnotation(val des: String = "def")
