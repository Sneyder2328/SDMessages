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

package com.sneyder.sdmessages.ui.conversation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.base.BaseActivity
import com.squareup.picasso.Picasso
import debug
import isJellyBeanOrLater
import isValidURL
import kotlinx.android.synthetic.main.activity_send_image.*
import java.io.File

class SendImageActivity : BaseActivity() {

    companion object {

        const val EXTRA_URL = "url"
        const val EXTRA_PATH = "path"

        fun starterIntent(context: Context, url: String = "", path: String = ""): Intent {
            val starter = Intent(context, SendImageActivity::class.java)
            starter.putExtra(EXTRA_URL, url)
            starter.putExtra(EXTRA_PATH, path)
            return starter
        }

    }

    private val url by lazy { intent.getStringExtra(EXTRA_URL) }
    private val path by lazy { intent.getStringExtra(EXTRA_PATH) }
    private var fileImage: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_image)
        setSupportActionBar(toolbar)

        if(url.isNotBlank()){
            Picasso.with(this).load(url).fit().centerInside().into(contentImageView)
        } else {
            fileImage = File(path)
            debug("send file $fileImage")
            Picasso.with(this).load(fileImage).fit().centerInside().into(contentImageView)
        }

        sendImageButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_URL, url).putExtra(EXTRA_PATH, path))
            finish()
        }

        if(isJellyBeanOrLater()){
            overlapNavigationAndStatusBar()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun overlapNavigationAndStatusBar() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION xor View.SYSTEM_UI_FLAG_FULLSCREEN xor View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = uiOptions
    }

    override fun onBackPressed() {
        // When pressing back
        // If path contains my package name(it means that the file is in my app internal storage)
        if(path.contains("com.sneyder.sdmessages")) {
            // then delete the image file to save space
            val deleteImage = fileImage?.delete()
            debug("file deleted $deleteImage")
        }
        super.onBackPressed()
    }

}
