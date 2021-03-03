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

package com.sneyder.sdmessages.ui.main.new_group

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.ui.register.AddUrlImgDialog
import com.sneyder.sdmessages.utils.*
import com.sneyder.sdmessages.utils.dialogs.ProfilePhotoDialog
import com.squareup.picasso.Picasso
import debug
import into
import kotlinx.android.synthetic.main.activity_new_group.*
import load
import toast
import java.io.File

class NewGroupActivity : DaggerActivity(), ProfilePhotoDialog.SelectImageListener, AddUrlImgDialog.AddUrlImgListener {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, NewGroupActivity::class.java)
        }

    }

    private val newGroupViewModel by lazy { getViewModel<NewGroupViewModel>() }
    private var fileImgToUpload: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        imgGroupCameraButton.setOnClickListener {
            ProfilePhotoDialog.newInstance().show(supportFragmentManager, "ProfilePhotoDialog")
        }
        newGroupViewModel.groupInfo.observe(this, Observer { group ->
            group.ifSuccess { finish() }
            group.ifError { it?.let{ toast(it) } }
            group.ifLoading { debug("loading") }
        })
    }

    private fun createGroup() {
        val groupName = groupNameEditText.text.toString()
        if (groupName.isBlank()){
            groupNameEditText.error = getString(R.string.new_group_warning_missing_group_name)
            return
        }
        var pictureUrl = ""
        // if there's an image to upload, then upload it
        fileImgToUpload?.let {
            val key = generateKeyForS3()
            pictureUrl = getS3BucketWithKey(key)
            newGroupViewModel.uploadImage(it, key)
        }
        newGroupViewModel.createGroup(groupName, pictureUrl)
    }

    override fun onTakePicture() {
        launchTakePictureIntent()
    }

    override fun onPickImage() {
        launchImageSelectorIntent()
    }

    override fun onShowAddUrlImageDialog() {
        AddUrlImgDialog.newInstance().show(supportFragmentManager, "AddUrlImgDialog")
    }

    override fun onUrlImgAdded(urlImg: String) {
        load(urlImg).into(imgGroupImageView)
    }

    override fun onActivityResultWithImageFile(imgPickedOrTaken: File) {
        debug("onActivityResultWithImageFile $imgPickedOrTaken")
        fileImgToUpload = imgPickedOrTaken
        Picasso.with(this).load(imgPickedOrTaken).fit().centerCrop().into(imgGroupImageView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_group, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.action_create_group -> createGroup()
        }
        return super.onOptionsItemSelected(item)
    }

}
