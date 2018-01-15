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

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.MenuItem
import android.view.View
import android.view.View.*
import com.sneyder.sdmessages.R
import com.squareup.picasso.Picasso
import isJellyBeanOrLater
import kotlinx.android.synthetic.main.activity_view_picture.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ViewPictureActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_IMAGE_TO_VIEW = "imageToView"

        fun starterIntent(context: Context, img: String): Intent {
            val starter = Intent(context, ViewPictureActivity::class.java)
            starter.putExtra(EXTRA_IMAGE_TO_VIEW, img)
            return starter
        }

    }

    private val img by lazy { intent.getStringExtra(EXTRA_IMAGE_TO_VIEW) }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_picture)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Picasso.with(this).load(img).fit().centerInside().into(fullscreenImageView)

        if(isJellyBeanOrLater()){
            overlapNavigationAndStatusBar()
        }
    }

    private fun overlapNavigationAndStatusBar() {
        val decorView = window.decorView
        val uiOptions = SYSTEM_UI_FLAG_HIDE_NAVIGATION xor View.SYSTEM_UI_FLAG_FULLSCREEN xor SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = uiOptions
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
