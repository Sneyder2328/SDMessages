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

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.DatePicker
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.*
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.ui.main.MainActivity
import com.sneyder.sdmessages.ui.signup.SignUpActivity
import com.sneyder.sdmessages.utils.*
import com.squareup.picasso.Picasso
import debug
import generateUUID
import gone
import isValidEmail
import isValidURL
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.iid.FirebaseInstanceId
import com.sneyder.sdmessages.utils.dialogs.SelectImageDialog
import snackBar
import toast
import java.io.File
import java.util.*

class RegisterActivity : DaggerActivity(), android.app.DatePickerDialog.OnDateSetListener, SelectImageDialog.SelectImageListener, AddUrlImgDialog.AddUrlImgListener {

    companion object {

        private const val ARG_TYPE_LOGIN = "TypeLogin"
        private const val ARG_USER_ID = "userId"
        private const val ARG_EMAIL = "email"
        private const val ARG_USERNAME = "username"
        private const val ARG_PHOTO_URL = "photoUrl"
        private const val ARG_ACCESS_TOKEN = "accessToken"

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

    private val registerViewModel by lazy { getViewModel<RegisterViewModel>() }
    private var fileImgToUpload: File? = null
    private var validDateOfBirth: Long? = null
    private val typeLogin: String by lazy { intent.getStringExtra(ARG_TYPE_LOGIN) }
    private val userId: String by lazy { intent.getStringExtra(ARG_USER_ID) ?: generateUUID() }
    private val email: String? by lazy { intent.getStringExtra(ARG_EMAIL) }
    private val username: String? by lazy { intent.getStringExtra(ARG_USERNAME) }
    private val accessToken: String by lazy { intent.getStringExtra(ARG_ACCESS_TOKEN) ?: "" }
    private var photoUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initAWSMobileClient()

        setUpPhotoUrl()

        // allow link in the privacy policy to be clickable
        policyTextView.movementMethod = LinkMovementMethod.getInstance()

        birthDateTextView.setOnClickListener {
            com.sneyder.sdmessages.utils.dialogs.DatePickerDialog().show(supportFragmentManager, "DatePickerDialog")
        }

        imgProfileCameraButton.setOnClickListener {
            showAddProfileImgDialog()
        }

        setUpLoginMode(typeLogin)

        registerViewModel.userInfo.observe(this, Observer { user ->
            user.ifSuccess { openMainActivity() }
            user.ifError {
                it?.let{ toast(it) }
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
        SelectImageDialog.newInstance().show(supportFragmentManager, "SelectImageDialog")
    }

    override fun onTakePicture(){
        launchTakePictureIntent()
    }

    override fun onPickImage(){
        launchImageSelectorIntent()
    }

    override fun onShowAddUrlImageDialog(){
        AddUrlImgDialog.newInstance().show(supportFragmentManager, "AddUrlImgDialog")
    }

    override fun onUrlImgAdded(urlImg: String) {
        photoUrl = urlImg
        updateImgProfile(photoUrl)
        fileImgToUpload = null
    }

    override fun onActivityResultWithImageFile(imgPickedOrTaken: File) {
        debug("onActivityResultWithImageFile $imgPickedOrTaken")
        fileImgToUpload = imgPickedOrTaken
        updateImgProfile(fileImgToUpload!!.absolutePath)
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
        registerViewModel.signUp(UserRequest(userId = userId, password = password, email = email, accessToken = accessToken, firebaseTokenId = FirebaseInstanceId.getInstance().token ?: "",
                displayName = username, birthDate = validDateOfBirth!!.toString(), typeLogin = typeLogin, photoUrl = photoUrl))
    }

    private fun updateImgProfile(pathOrUrl: String) {
        debug("updateImgProfile pathOrUrl = $pathOrUrl")
        if(pathOrUrl.isValidURL()) {
            Picasso.with(this@RegisterActivity).load(pathOrUrl).fit().centerCrop().into(imgProfileImageView)
        }
        else{
            Picasso.with(this@RegisterActivity).load(File(pathOrUrl)).fit().centerCrop().into(imgProfileImageView)
        }
        imgProfileCameraButton.gone()
    }

}
