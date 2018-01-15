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

package com.sneyder.sdmessages.ui.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.PermissionsManager
import com.sneyder.sdmessages.ui.camera.TakePictureActivity
import com.sneyder.sdmessages.utils.AUTHORITIES_FILE_PROVIDER
import com.sneyder.sdmessages.utils.ImageSelectorUtils
import createImageFile
import debug
import isOreoOrLater
import newThread
import toast
import java.io.File
import java.io.IOException


abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PICK_PHOTO = 100
        const val REQUEST_TAKE_PHOTO = 101
    }

    private var isPermissionsManagerInitialized: Boolean = false

    val permissionsManager by lazy {
        isPermissionsManagerInitialized = true
        PermissionsManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isOreoOrLater()) createNotificationChannels()
    }

    /**
     * Set all the necessary notification channels and is executed in the onCreate function of DaggerActivity
     * This function should be overridden in the Main Activity and according to Google there is no problem when It is called in every onCreate() because It doesn't perform anything if the channels original values haven't changed
     */
    @RequiresApi(Build.VERSION_CODES.O)
    open fun createNotificationChannels() {
    }

    fun initAWSMobileClient() = newThread { AWSMobileClient.getInstance().initialize(this).execute() }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionsManagerInitialized) permissionsManager.onRequestPermissionsResult(requestCode, grantResults)
    }

    fun launchTakePictureIntent() {
        permissionsManager.ifHasPermission(arrayOf(PermissionsManager.WRITE_EXTERNAL_STORAGE, PermissionsManager.CAMERA), REQUEST_TAKE_PHOTO, {
            // If the app got the permission, dispatch the capture image intent
            startActivityForResult(TakePictureActivity.starterIntent(this), REQUEST_TAKE_PHOTO)
        })
    }

    fun launchImageSelectorIntent(){
        permissionsManager.ifHasPermission(PermissionsManager.READ_EXTERNAL_STORAGE, REQUEST_PICK_PHOTO, {
            val intent = ImageSelectorUtils.getImageSelectionIntent()
            startActivityForResult(intent, REQUEST_PICK_PHOTO)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || data == null) return
        val imgPickedOrTaken = when (requestCode){
            REQUEST_TAKE_PHOTO -> { // a new photo has been taken
                val path = data.getStringExtra(TakePictureActivity.EXTRA_PATH_PICTURE)
                File(path)
            }
            REQUEST_PICK_PHOTO -> { // a existing image has been selected
                val selectedImage: Uri? = data.data
                val path = ImageSelectorUtils.getFilePathFromUri(this, selectedImage)
                File(path) // get file pointing to it
            }
            else -> null
        }
        debug("imgPickedOrTaken = $imgPickedOrTaken")
        imgPickedOrTaken?.let { onActivityResultWithImageFile(it) }
    }

    open fun onActivityResultWithImageFile(imgPickedOrTaken: File) {}
}