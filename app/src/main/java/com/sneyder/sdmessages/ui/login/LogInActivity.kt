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

package com.sneyder.sdmessages.ui.login

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.TypeLogin
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.ui.home.HomeActivity
import com.sneyder.sdmessages.ui.main.MainActivity
import com.sneyder.sdmessages.ui.signup.SignUpActivity
import com.sneyder.sdmessages.utils.ifError
import com.sneyder.sdmessages.utils.ifLoading
import com.sneyder.sdmessages.utils.ifSuccess
import kotlinx.android.synthetic.main.activity_log_in.*
import org.json.JSONException
import toast
import debug
import error
import isValidEmail

class LogInActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, LogInActivity::class.java)
        }

        const val RC_LOG_IN = 1

    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val logInViewModel by lazy { getViewModel<LogInViewModel>() }
    private val callbackManager by lazy { CallbackManager.Factory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        initAWSMobileClient()
        setUpGoogleLogin()
        setUpFacebookLogin()
        logInViewModel.myUserInfo.observe(this, Observer { user ->
            user.ifLoading {
                logInButton.isEnabled = false
                logInWithFacebookButton.isEnabled = false
                logInWithGoogleButton.isEnabled = false
            }
            user.ifSuccess { openMainActivity() }
            user.ifError {
                it?.let{ toast(it) }
                logInButton.isEnabled = true
                logInWithFacebookButton.isEnabled = true
                logInWithGoogleButton.isEnabled = true
            }
        })
    }

    private fun setUpFacebookLogin() {
        logInWithFacebookButton.setReadPermissions("email")
        // Callback registration

        logInWithFacebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookSignInResult(loginResult.accessToken)
            }
            override fun onCancel() {
                debug("onCancel FacebookCallback")
            }
            override fun onError(exception: FacebookException) {
                error("onError FacebookCallback ${exception.message}")
                exception.printStackTrace()
            }
        })
    }

    private fun handleFacebookSignInResult(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(currentAccessToken) { jsonObject, _ ->
            try {
                val email = jsonObject.getString("email")
                val id = jsonObject.getString("id")
                debug("onSuccess FacebookCallback token=${currentAccessToken.token}")
                debug("newMeRequest email=$email id=$id")
                LoginManager.getInstance().logOut() // Logged in successfully, logOut and open RegisterActivity.
                logInViewModel.logInUser(email = email, typeLogin =  TypeLogin.FACEBOOK.data, accessToken = currentAccessToken.token, userId = id, password = "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email")
        request.parameters = parameters
        request.executeAsync()
    }

    override fun onBackPressed() {
        startActivity(HomeActivity.starterIntent(this@LogInActivity))
        super.onBackPressed()
    }

    fun logIn(v: View) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        if (email.isBlank() || !email.isValidEmail()) {
            emailEditText.error = getString(R.string.login_warning_invalid_email)
            return
        }
        if (password.isBlank()) {
            passwordEditText.error = getString(R.string.login_warning_missing_password)
            return
        }
        logInViewModel.logInUser(email, password, TypeLogin.EMAIL.data)
    }

    private fun setUpGoogleLogin() {
        // Configure sign-in to request the myUserInfo's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun logInWithGoogle(v: View) {
        logInWithGoogleButton.isEnabled = false
        startActivityForResult(googleSignInClient.signInIntent, RC_LOG_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUpActivity.RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(result)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleGoogleSignInResult(result: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount = result!!.getResult(ApiException::class.java)
            val idToken = account.idToken!!
            debug("handleGoogleSignInResult idToken = $idToken userId = ${account.id}")
            logInViewModel.logInUser(email = account.email!!, password = "", typeLogin = TypeLogin.GOOGLE.data, accessToken = idToken, userId = account.id!!)
            googleSignInClient.signOut() // logged  in successfully, logOut and open RegisterActivity.
        } catch (e: ApiException) {
            // The ApiException syncStatus code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            toast(R.string.signup_message_unable_google_login)
            error("error handleGoogleSignInResult code = ${e.statusCode}")
            e.printStackTrace()
        } catch (e: Exception){
            toast(R.string.signup_message_unable_google_login)
            error("error handleGoogleSignInResult e = ${e.message}")
            e.printStackTrace()
        }
    }

    private fun openMainActivity() {
        startActivity(MainActivity.starterIntent(this@LogInActivity))
        finish()
    }

}
