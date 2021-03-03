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

package com.sneyder.sdmessages.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.GoogleApiAvailability
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.ui.login.LogInActivity
import com.sneyder.sdmessages.ui.main.MainActivity
import com.sneyder.sdmessages.ui.signup.SignUpActivity
import debug

class HomeActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }

    }

    private val homeViewModel by lazy { getViewModel<HomeViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        GoogleApiAvailability.getInstance().setDefaultNotificationChannelId(this, "Channel")
        homeViewModel.ifLogged {
            openMainActivity()
        }
    }

    fun signUp(v: View) {
        openSignUpActivity()
    }

    fun logIn(v: View) {
        openLogInActivity()
    }

    private fun openSignUpActivity() {
        startActivity(SignUpActivity.starterIntent(this@HomeActivity))
        finish()
    }

    private fun openLogInActivity() {
        startActivity(LogInActivity.starterIntent(this@HomeActivity))
        finish()
    }

    private fun openMainActivity() {
        startActivity(MainActivity.starterIntent(this@HomeActivity))
        finish()
    }
}