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

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sneyder.sdmessages.R

class HomeActivity : AppCompatActivity() {

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun signUp(v: View) {
        openSignUpActivity()
        v.isEnabled = false
    }

    fun logIn(v: View) {
        openLogInActivity()
        v.isEnabled = false
    }

    private fun openSignUpActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun openLogInActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}