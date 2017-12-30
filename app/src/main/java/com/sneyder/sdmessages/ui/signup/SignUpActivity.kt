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

package com.sneyder.sdmessages.ui.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.facebook.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.TypeLogin
import com.sneyder.sdmessages.ui.base.BaseActivity
import com.sneyder.sdmessages.ui.home.HomeActivity
import com.sneyder.sdmessages.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import error
import toast
import com.facebook.login.LoginResult
import debug
import org.json.JSONException
import org.json.JSONObject


class SignUpActivity : BaseActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }

        const val RC_SIGN_IN = 1

    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val callbackManager by lazy { CallbackManager.Factory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setUpGoogleLogin()
        setUpFacebookLogin()
    }

    private fun setUpFacebookLogin() {
        signUpFacebookButton.setReadPermissions("email")
        // Callback registration

        signUpFacebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
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
        val request = GraphRequest.newMeRequest(currentAccessToken) { jsonObject, response ->
            try {
                val picture: JSONObject? = jsonObject.getJSONObject("picture")
                val url = picture?.getJSONObject("data")?.get("url")?.toString() ?: ""
                val name = jsonObject.getString("name")
                val email = jsonObject.getString("email")
                val id = jsonObject.getString("id")
                debug("onSuccess FacebookCallback token=${currentAccessToken.token}")
                debug("newMeRequest name=$name email=$email id=$id picture=$picture  url = $url")
                LoginManager.getInstance().logOut() // Signed in successfully, logOut and open RegisterActivity.
                startActivity(RegisterActivity.starterIntent(this@SignUpActivity, TypeLogin.FACEBOOK.data, email = email,
                        userId = id, username = name, photoUrl = url, accessToken = currentAccessToken.token))
                finish()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture.width(512)")
        request.parameters = parameters
        request.executeAsync()
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

    fun signUpGoogle(v: View) {
        signUpGoogleButton.isEnabled = false
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(result)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleGoogleSignInResult(result: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount = result!!.getResult(ApiException::class.java)
            debug("handleGoogleSignInResult idToken = ${account.idToken} userId = ${account.id}")
            googleSignInClient.signOut() // Signed in successfully, logOut and open RegisterActivity.
            startActivity(RegisterActivity.starterIntent(this@SignUpActivity, TypeLogin.GOOGLE.data, email = account.email,
                    userId = account.id, username = account.displayName, photoUrl = account.photoUrl.toString(), accessToken = account.idToken))
            finish()
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

    fun signUpEmail(v: View) {
        startActivity(RegisterActivity.starterIntent(this@SignUpActivity))
        finish()
    }

    override fun onBackPressed() {
        startActivity(HomeActivity.starterIntent(this@SignUpActivity))
        super.onBackPressed()
    }
}
