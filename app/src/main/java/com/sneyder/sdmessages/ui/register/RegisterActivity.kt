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

package com.sneyder.sdmessages.ui.register

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import com.amazonaws.mobile.client.AWSMobileClient
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.PermissionsManager
import com.sneyder.sdmessages.data.model.*
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.ui.main.MainActivity
import com.sneyder.sdmessages.ui.signup.SignUpActivity
import com.sneyder.sdmessages.utils.*
import com.squareup.picasso.Picasso
import createImageFile
import debug
import generateUUID
import gone
import isValidEmail
import isValidURL
import kotlinx.android.synthetic.main.activity_register.*
import onChangeListener
import snackBar
import toast
import java.io.File
import java.io.IOException
import java.util.*

class RegisterActivity : DaggerActivity(), DatePickerDialog.OnDateSetListener {

    companion object {

        private const val ARG_TYPE_LOGIN = "TypeLogin"
        private const val ARG_USER_ID = "userId"
        private const val ARG_EMAIL = "email"
        private const val ARG_USERNAME = "username"
        private const val ARG_PHOTO_URL = "photoUrl"
        private const val ARG_ACCESS_TOKEN = "accessToken"

        private const val REQUEST_PICK_PHOTO = 100
        private const val REQUEST_TAKE_PHOTO = 101

        fun starterIntent(context: Context, typeLogin: String = TypeLogin.EMAIL.data, userId: String? = null,
                          email: String? = null, username: String? = null, photoUrl: String? = null, accessToken: String? = null
        ) = Intent(context, RegisterActivity::class.java).apply {
            putExtra(ARG_TYPE_LOGIN, typeLogin)
            userId?.let { putExtra(ARG_USER_ID, it) }
            email?.let { putExtra(ARG_EMAIL, it) }
            username?.let { putExtra(ARG_USERNAME, it) }
            photoUrl?.let { putExtra(ARG_PHOTO_URL, it) }
            accessToken?.let { putExtra(ARG_ACCESS_TOKEN, it) }
            }
    }

    private val registerViewModel by lazy { getViewModel(RegisterViewModel::class.java) }
    private var fileImgToUpload: File? = null
    private var validDateOfBirth: Long? = null
    private val typeLogin: String by lazy { intent.getStringExtra(ARG_TYPE_LOGIN) }
    private val userId: String by lazy { intent.getStringExtra(ARG_USER_ID) ?: generateUUID() }
    private val email: String? by lazy { intent.getStringExtra(ARG_EMAIL) }
    private val username: String? by lazy { intent.getStringExtra(ARG_USERNAME) }
    private val accessToken: String by lazy { intent.getStringExtra(ARG_ACCESS_TOKEN) ?: "" }
    private var photoUrl: String = ""

    private val permissionsManager by lazy { PermissionsManager(this@RegisterActivity, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        AWSMobileClient.getInstance().initialize(this).execute()

        setUpPhotoUrl()

        policyTextView.movementMethod = LinkMovementMethod.getInstance()

        birthDateTextView.setOnClickListener {
            val dialogDate = DialogDate()
            dialogDate.show(fragmentManager, "tag")
        }

        imgProfileImageView.setOnClickListener {
            showAddProfileImgDialog()
        }

        setUpLoginMode(typeLogin)

        registerViewModel.userInfo.observe(this, Observer { user ->
            user.ifSuccess { openMainActivity() }
            user.ifError {
                toast(it)
                signUpButton.isEnabled = true
            }
            user.ifLoading { signUpButton.isEnabled = false }
        })
    }

    private fun setUpPhotoUrl() {
        photoUrl = intent.getStringExtra(ARG_PHOTO_URL) ?: ""
        if (photoUrl.isNotBlank()) updateImgProfile(photoUrl)
    }

    private fun showAddProfileImgDialog() {
        AlertDialog.Builder(this).setItems(R.array.register_add_profile_img_options, { _, item ->
            when (item) {
                0 -> { takePhoto() }
                1 -> { pickPhoto() }
                2 -> showDialogAddUrlImage()
            }
        }).create().show()
    }

    private var newPhotoTakenFile: File? = null
    private fun takePhoto() {
        permissionsManager.ifHasPermission(PermissionsManager.WRITE_EXTERNAL_STORAGE, 0, {
            debug("it has WRITE_EXTERNAL_STORAGE ")
            // If the app got the permission, dispatch the capture image intent
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                try {
                    newPhotoTakenFile = createImageFile(this@RegisterActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    toast(R.string.register_message_error_taking_photo)
                    return@ifHasPermission
                }
                // Continue only if the File was successfully created
                val photoURI = FileProvider.getUriForFile(this,
                        AUTHORITIES_FILE_PROVIDER,
                        newPhotoTakenFile!!)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        })
    }

    @SuppressLint("NewApi")
    private fun pickPhoto() {
        permissionsManager.ifHasPermission(PermissionsManager.READ_EXTERNAL_STORAGE, 1, {
            debug("it has READ_EXTERNAL_STORAGE ")
            launchImageSelectorIntent()
        })
    }

    private fun launchImageSelectorIntent() {
        val intent = ImageSelectorUtils.getImageSelectionIntent()
        startActivityForResult(intent, REQUEST_PICK_PHOTO)
    }

    @SuppressLint("InflateParams")
    private fun showDialogAddUrlImage(){
        val layoutInflater = LayoutInflater.from(this)
        val v = layoutInflater.inflate(R.layout.activity_register_add_image_url_dialog, null)

        val imagePreviewImageView: CircleImageView = v.findViewById(R.id.imagePreviewImageView)
        val imageUrlEditText: EditText = v.findViewById(R.id.imageUrlEditText)
        imageUrlEditText.onChangeListener({
            Picasso.with(this@RegisterActivity).load(it.toString()).into(imagePreviewImageView)
        })

        AlertDialog.Builder(this)
                .setView(v)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    photoUrl = imageUrlEditText.text.toString()
                    updateImgProfile(photoUrl)
                    fileImgToUpload = null
                }
                .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _ -> dialog.cancel() }
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || data == null) return
        fileImgToUpload = when (requestCode){
            REQUEST_TAKE_PHOTO -> { // a new photo has been taken
                newPhotoTakenFile // use the file used to save the photo taken
            }
            else -> { // a existing image has been selected
                val selectedImage = data.data
                val path = ImageSelectorUtils.getFilePathFromUri(this, selectedImage)
                File(path) // get file pointing to it
            }
        }
        updateImgProfile(fileImgToUpload!!.absolutePath)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onDateSet(d: DatePicker, year: Int, month: Int, day: Int) = setDate(day, month, year)

    override fun onBackPressed() {
        startActivity(SignUpActivity.starterIntent(this@RegisterActivity))
        super.onBackPressed()
    }

    private fun setDate(day: Int, month: Int, year: Int) {
        val stringDate = String.format(getString(R.string.register_format_birthday), day, month + 1, year)
        birthDateTextView.text = stringDate

        val birthDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        val birthDateMore18 = Calendar.getInstance().apply {
            set(Calendar.YEAR, year + 18)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        val dateNow = Calendar.getInstance()
        if (dateNow.after(birthDateMore18)) { // if the myUserInfo is 18 years old or above, save its valid birthday
            validDateOfBirth = birthDate.timeInMillis
        } else { // otherwise show that he/she must be at least 18 years old to use this app
            snackBar(getString(R.string.register_warning_invalid_birthday))
        }
    }

    private fun setUpLoginMode(typeLogin: String?) {
        when (typeLogin) {
            TypeLogin.GOOGLE.data, TypeLogin.FACEBOOK.data -> {
                passwordEditText.gone()
                usernameEditText.setText(username)
                emailEditText.setText(email)
                emailEditText.isEnabled = false
            }
        }
    }

    private fun openMainActivity() {
        startActivity(MainActivity.starterIntent(this@RegisterActivity))
        finish()
    }

    fun signUp(v: View) {
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isBlank()) {
            usernameEditText.error = getString(R.string.register_warning_missing_username)
            return
        }
        if (email.isBlank() || !email.isValidEmail()) {
            emailEditText.error = getString(R.string.register_warning_missing_email)
            return
        }
        if (typeLogin == TypeLogin.EMAIL.data && password.isBlank()) {
            passwordEditText.error = getString(R.string.register_warning_missing_password)
            return
        }
        if (validDateOfBirth == null) {
            snackBar(getString(R.string.register_warning_invalid_birthday))
            return
        }
        // if there's an image to upload, then upload it
        fileImgToUpload?.let {
            val key = generateKeyForS3()
            photoUrl = getS3BucketWithKey(key)
            registerViewModel.uploadImage(it, key)
        }
        registerViewModel.signUp(UserRequest(userId = userId, password = password, email = email, accessToken = accessToken,
                displayName = username, birthDate = validDateOfBirth!!.toString(), typeLogin = typeLogin, photoUrl = photoUrl))
    }

    private fun updateImgProfile(pathOrUrl: String) {
        debug("updateImgProfile pathOrUrl = $pathOrUrl")
        if(pathOrUrl.isValidURL()) {
            Picasso.with(this@RegisterActivity).load(pathOrUrl).into(imgProfileImageView)
        }
        else{
            Picasso.with(this@RegisterActivity).load(File(pathOrUrl)).into(imgProfileImageView)
        }
        imgProfileCameraImageView.gone()
    }

}