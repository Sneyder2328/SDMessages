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

package com.sneyder.sdmessages.ui.main.profile

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.base.BaseActivity
import com.sneyder.sdmessages.ui.base.DaggerFragment
import com.sneyder.sdmessages.ui.register.AddUrlImgDialog
import com.sneyder.sdmessages.utils.dialogs.EditNameDialog
import com.sneyder.sdmessages.utils.dialogs.ProfilePhotoDialog
import com.sneyder.sdmessages.utils.ifError
import com.sneyder.sdmessages.utils.ifLoading
import com.sneyder.sdmessages.utils.ifSuccess
import com.squareup.picasso.Picasso
import debug
import into
import kotlinx.android.synthetic.main.fragment_profile.*
import load
import reObserve
import setHeight
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : DaggerFragment(), EditNameDialog.EditNameListener, ProfilePhotoDialog.SelectImageListener, AddUrlImgDialog.AddUrlImgListener {

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment
         * @return A new instance of fragment ProfileFragment.
         */
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_profile

    private val profileViewModel by lazy { getViewModel(ProfileViewModel::class.java) }
    private var lastPhotoUrl = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profileViewModel.loadUserInfo()
        observeMyUserInfo()
        observeImgUploading()
        usernameTextView.setOnClickListener {
            EditNameDialog.newInstance(usernameTextView.text.toString()).show(childFragmentManager, "EditNameDialog")
        }
        imgProfileCameraButton.setOnClickListener {
            ProfilePhotoDialog.newInstance().show(childFragmentManager, "ProfilePhotoDialog")
        }
        // set the height of the imageView the same size as the width because the profile img is shown in a circle/square and different width/height would cause issues
        imgProfileImageView.post { imgProfileImageView.setHeight(imgProfileImageView.width) }
    }

    private fun observeMyUserInfo() {
        profileViewModel.myUserInfo.reObserve(this, Observer {
            it.ifSuccess { user ->
                usernameTextView.text = user.displayName
                usernameTextView.setTextColor(Color.BLACK)
                if (lastPhotoUrl != user.photoUrl) {
                    context?.load(user.photoUrl).into(imgProfileImageView, { fit().centerCrop()})
                    lastPhotoUrl = user.photoUrl?:""
                }
            }
        })
    }

    private fun observeImgUploading() {
        profileViewModel.imgUploading.reObserve(this, Observer {

        })
    }

    override fun onNewUserName(userName: String) {
        usernameTextView.setTextColor(Color.rgb(188, 188, 188))
        profileViewModel.updateUserName(userName)
    }

    override fun onTakePicture() {
        (activity as BaseActivity).launchTakePictureIntent()
    }

    override fun onPickImage() {
        (activity as BaseActivity).launchImageSelectorIntent()
    }

    override fun onShowAddUrlImageDialog() {
        AddUrlImgDialog.newInstance().show(childFragmentManager, "AddUrlImgDialog")
    }

    override fun onUrlImgAdded(urlImg: String) {
        context?.load(urlImg).into(imgProfileImageView)
    }

    fun onActivityResultWithImageFile(imgPickedOrTaken: File) {
        debug("onActivityResultWithImageFile $imgPickedOrTaken")
        profileViewModel.uploadImage(imgPickedOrTaken)
    }

}
