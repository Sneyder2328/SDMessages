/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneyder.sdmessages.ui.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.base.BaseActivity
import createImageFile
import debug
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.Flash
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_take_picture.*
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.result.WhenDoneListener
import isJellyBeanOrLater
import java.io.File


class TakePictureActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_PATH_PICTURE = "pathPicture"

        fun starterIntent(context: Context): Intent {
            return Intent(context, TakePictureActivity::class.java)
        }

    }

    private var isUsingBackLens = true
    private var isFlashOn = false

    private val cameraConfiguration = CameraConfiguration
            .builder()
            .photoResolution(
                    firstAvailable(
                            wideRatio(firstAvailable(highestResolution(), lowestResolution())), standardRatio(firstAvailable(highestResolution(), lowestResolution()))
                    )
            )
           .focusMode(
                   firstAvailable(
                           continuousFocusPicture(),
                           autoFocus(),
                           fixed()
                   )
           )
            .flash(off())
            .previewFpsRange(highestFps())
            .sensorSensitivity(highestSensorSensitivity())
            .build()

    private val fotoapparat by lazy {
        Fotoapparat(context = this, view = cameraView, lensPosition = back(), scaleType = ScaleType.CenterCrop, cameraConfiguration = cameraConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)

        takePictureButton.setOnClickListener {
            takePicture()
        }

        switchCameraButton.setOnClickListener {
            changeCamera()
        }

        switchFlashButton.setOnClickListener {
            toggleFlash()
        }

        adjustViewsVisibility()
        if(isJellyBeanOrLater()) overlapNavigationAndStatusBar()
    }

    private fun overlapNavigationAndStatusBar() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }
    private var file: File? = null

    private fun takePicture() {
        file = createImageFile()
        fotoapparat.takePicture()
                .saveToFile(file!!)
                .whenDone( object : WhenDoneListener<Unit> {
                    override fun whenDone(it: Unit?) {
                        debug("whenAvailable photoResult")
                        setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_PATH_PICTURE, file!!.absolutePath))
                        finish()
                    }
                })
    }

    private fun changeCamera() {
        fotoapparat.switchTo(
                if(isUsingBackLens) front() else back(),
                cameraConfiguration
        )
        isUsingBackLens = !isUsingBackLens
        adjustViewsVisibility()
    }

    private fun adjustViewsVisibility() {
        fotoapparat.getCapabilities()
                .whenAvailable { capabilities ->
                    capabilities?.let {
                        switchFlashButton.visibility = if (it.flashModes.contains(Flash.Torch)) View.VISIBLE else View.GONE
                    }
                }
        switchCameraButton.visibility = if (fotoapparat.isAvailable(front())) View.VISIBLE else View.GONE
    }

    private fun toggleFlash() {
        fotoapparat.updateConfiguration(
                UpdateConfiguration(flashMode = if (isFlashOn) off() else torch())
        )
        isFlashOn = !isFlashOn
        debug("Flash is now $isFlashOn")
        switchFlashButton.setImageResource(if(isFlashOn) R.drawable.ic_flash_off else R.drawable.ic_flash_on)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }

    override fun onBackPressed() {
        debug("file deleted = ${file?.delete()}")
        super.onBackPressed()
    }
}