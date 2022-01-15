package com.angcyo.uicore.demo

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.angcyo.core.component.DslFinger
import com.angcyo.github.biometric.BiometricAuth
import com.angcyo.github.biometric.BiometricAuthenticationCancelledException
import com.angcyo.github.biometric.BiometricAuthenticationException
import com.angcyo.github.finger.FPerException
import com.angcyo.github.finger.IdentificationInfo
import com.angcyo.github.finger.RxFingerPrinter
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.FingerPrinterView
import com.angcyo.widget.span.span
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import java.io.IOException
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.security.cert.CertificateException


/**
 * 生物识别
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/01/15
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class BiometricDemo : AppDslFragment() {

    var fingerPrinter: RxFingerPrinter? = null

    var biometricPrinterView: FingerPrinterView? = null
    var biometricPrinterView2: FingerPrinterView? = null

    var fingerPrinterView: FingerPrinterView? = null
    var fingerPrinterView2: FingerPrinterView? = null

    private val cryptoManager: CryptoManager by lazy {
        CryptoManager()
    }

    private val biometricAuth: BiometricAuth by lazy {
        BiometricAuth.create(requireActivity(), useAndroidXBiometricPrompt = true)
    }

    private val biometricAuth2: BiometricAuth by lazy {
        BiometricAuth.create(requireActivity(), useAndroidXBiometricPrompt = false)
    }

    init {
        contentLayoutId = R.layout.fragment_biometric
    }

    @SuppressLint("CheckResult")
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.post {
            //注意此处
            fingerPrinter = RxFingerPrinter(requireActivity())
        }

        fingerPrinterView = _vh.v<FingerPrinterView>(R.id.finger_printer_view)
        fingerPrinterView2 = _vh.v<FingerPrinterView>(R.id.finger_printer_view2)
        biometricPrinterView = _vh.v<FingerPrinterView>(R.id.biometric_printer_view)
        biometricPrinterView2 = _vh.v<FingerPrinterView>(R.id.biometric_printer_view2)

        val from = FingerprintManagerCompat.from(fContext())

        _vh.tv(R.id.tip_view)?.text = span {
            append("硬件:" + from.isHardwareDetected)
            appendln()
            append("指纹:" + from.hasEnrolledFingerprints())
        }

        //新版api
        _vh.click(R.id.biometric_printer_view) {
            biometricPrinterView?.state = FingerPrinterView.STATE_SCANING
            testAuthenticateWithCrypto()
        }

        //新版api 2
        _vh.click(R.id.biometric_printer_view2) {
            biometricPrinterView2?.state = FingerPrinterView.STATE_SCANING
            testAuthenticateWithCrypto2()
        }

        //旧版api
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
                        onResult("指纹识别成功")
                    } else {
                        val exception: FPerException? = info.exception
                        if (exception != null) {
                            onResult(exception.displayMessage)
                            fingerPrinterView?.state = FingerPrinterView.STATE_WRONG_PWD
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    onResult(e.message ?: "error")
                }

                override fun onComplete() {
                    onResult("complete")
                }

            })
        }

        _vh.click(R.id.finger_printer_view2) {
            fingerPrinterView2?.state = FingerPrinterView.STATE_SCANING
            DslFinger().startAuthenticate(fContext()).subscribe { result ->
                if (result.success) {
                    fingerPrinterView2?.state = FingerPrinterView.STATE_CORRECT_PWD
                    onResult("识别成功")
                } else {
                    fingerPrinterView2?.state = FingerPrinterView.STATE_WRONG_PWD
                    onResult("识别失败:${"${result.error?.errMsgId},${result.error?.message}"}")
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun testAuthenticateWithCrypto() {
        if (biometricAuth.hasFingerprintHardware.not()) {
            toastQQ("Devices provides no fingerprint hardware")
        } else if (biometricAuth.hasFingerprintsEnrolled.not()) {
            toastQQ("No fingerprints enrolled")
        } else {
            biometricAuth.authenticate(
                cryptoObject = BiometricAuth.Crypto(cryptoManager.cipher),
                title = "Please authenticate",
                subtitle = "Using 'Awesome Feature' requires your authentication.",
                description = "'Awesome Feature' exposes data private to you, which is why you need to authenticate.",
                negativeButtonText = "Cancel",
                prompt = "Touch the fingerprint sensor",
                notRecognizedErrorText = "Not recognized"
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        biometricPrinterView?.state = FingerPrinterView.STATE_CORRECT_PWD
                        onResult("Success!")
                    },
                    { throwable ->
                        biometricPrinterView?.state = FingerPrinterView.STATE_WRONG_PWD
                        when (throwable) {
                            is BiometricAuthenticationException -> {
                                onResult("Error: ${throwable.errorString}")
                            }
                            is BiometricAuthenticationCancelledException -> {
                                onResult("Cancelled")
                            }
                            else -> onResult("onError:${throwable}")
                        }
                    }
                )
        }
    }

    @SuppressLint("CheckResult")
    private fun testAuthenticateWithCrypto2() {
        if (biometricAuth2.hasFingerprintHardware.not()) {
            toastQQ("Devices provides no fingerprint hardware")
        } else if (biometricAuth2.hasFingerprintsEnrolled.not()) {
            toastQQ("No fingerprints enrolled")
        } else {
            biometricAuth2.authenticate(
                title = "title",
                subtitle = "subtitle",
                description = "description",
                negativeButtonText = "Cancel",
                prompt = "Touch the fingerprint sensor",
                notRecognizedErrorText = "Not recognized"
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        biometricPrinterView2?.state = FingerPrinterView.STATE_CORRECT_PWD
                        onResult("Success!")
                    },
                    { throwable ->
                        biometricPrinterView2?.state = FingerPrinterView.STATE_WRONG_PWD
                        when (throwable) {
                            is BiometricAuthenticationException -> {
                                onResult("Error: ${throwable.errorString}")
                            }
                            is BiometricAuthenticationCancelledException -> {
                                onResult("Cancelled")
                            }
                            else -> onResult("onError:${throwable}")
                        }
                    }
                )
        }
    }

    fun onResult(text: String) {
        toastQQ(text)
        _vh.tv(R.id.result_view)?.text = "${nowTimeString()} -> $text"
    }

    private class CryptoManager {

        companion object {
            private const val KEY_NAME = "test-key"
        }

        private var keyStore: KeyStore? = null
        private var keyGenerator: KeyGenerator? = null


        val cipher: Cipher by lazy {
            generateKey()
            initCipher()
        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun generateKey() {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore").also { keyStore ->
                    keyStore.load(null)
                }
                keyGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                        .also { keyGenerator ->
                            keyGenerator?.init(
                                KeyGenParameterSpec.Builder(
                                    KEY_NAME,
                                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                                ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                    .setUserAuthenticationRequired(true)
                                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                    .build()
                            )
                            keyGenerator?.generateKey()
                        }
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: NoSuchProviderException) {
                e.printStackTrace()
            } catch (e: InvalidAlgorithmParameterException) {
                e.printStackTrace()
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun initCipher(): Cipher {
            try {
                val cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7
                )

                keyStore!!.load(null)
                val key = keyStore!!.getKey(KEY_NAME, null) as SecretKey
                cipher.init(Cipher.ENCRYPT_MODE, key)
                return cipher
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to init cipher", e)
            } catch (e: NoSuchPaddingException) {
                throw RuntimeException("Failed to init cipher", e)
            }
        }
    }

}