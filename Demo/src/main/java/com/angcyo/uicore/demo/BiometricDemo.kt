package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.angcyo.github.finger.FPerException
import com.angcyo.github.finger.IdentificationInfo
import com.angcyo.github.finger.RxFingerPrinter
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.FingerPrinterView
import com.angcyo.widget.span.span
import io.reactivex.observers.DisposableObserver


/**
 * 生物识别
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/01/15
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class BiometricDemo : AppDslFragment() {

    var fingerPrinter: RxFingerPrinter? = null

    var fingerPrinterView: FingerPrinterView? = null

    init {
        contentLayoutId = R.layout.fragment_biometric
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.post {
            //注意此处
            fingerPrinter = RxFingerPrinter(requireActivity())
        }

        fingerPrinterView = _vh.v<FingerPrinterView>(R.id.finger_printer_view)

        val from = FingerprintManagerCompat.from(fContext())

        _vh.tv(R.id.tip_view)?.text = span {
            append("硬件:" + from.isHardwareDetected)
            append("指纹:" + from.hasEnrolledFingerprints())
        }

        _vh.click(R.id.finger_printer_view) {

            if (!from.isHardwareDetected) {
                toastQQ("无指纹模块")
                return@click
            }

            if (!from.hasEnrolledFingerprints()) {
                toastQQ("未注册指纹")
                return@click
            }

            fingerPrinter?.begin()?.subscribe(object : DisposableObserver<IdentificationInfo>() {

                override fun onStart() {
                    super.onStart()
                    if (fingerPrinterView?.state == FingerPrinterView.STATE_SCANING) {
                        return
                    } else if (fingerPrinterView?.state == FingerPrinterView.STATE_CORRECT_PWD
                        || fingerPrinterView?.state == FingerPrinterView.STATE_WRONG_PWD
                    ) {
                        fingerPrinterView?.state = FingerPrinterView.STATE_NO_SCANING
                    } else {
                        fingerPrinterView?.state = FingerPrinterView.STATE_SCANING
                    }
                }

                override fun onNext(info: IdentificationInfo) {
                    if (info.isSuccessful) {
                        fingerPrinterView?.state = FingerPrinterView.STATE_CORRECT_PWD
                        toastQQ("指纹识别成功")
                    } else {
                        val exception: FPerException? = info.exception
                        if (exception != null) {
                            toastQQ(exception.displayMessage)
                            fingerPrinterView?.state = FingerPrinterView.STATE_WRONG_PWD
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                }

            })
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}