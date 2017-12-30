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

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.hashGenerator.Hasher
import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.data.model.UserRequest
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import javax.inject.Inject

class LogInViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val hasher: Hasher,
        schedulersProvider: SchedulerProvider
) : BaseViewModel(schedulersProvider){

    val myUserInfo: MutableLiveData<Resource<UserInfo>> = MutableLiveData()

    fun logInUser(email: String, password: String, typeLogin: String, accessToken: String = "", userId: String = ""){
        add(userRepository.logInUser(UserRequest(email = email, password = hasher.hash(password + email), typeLogin = typeLogin, accessToken = accessToken, userId = userId))
                .applySchedulers()
                .subscribe({ userInfoResult: UserInfo ->
                    insertUserLoggedInDb(userInfoResult)
                }, {
                    myUserInfo.value = Resource.error(R.string.login_message_error_loggin_in)
                }))
    }

    private fun insertUserLoggedInDb(userInfoLoggedIn: UserInfo) {
        add(userRepository.insertUser(userInfoLoggedIn)
                .applySchedulers()
                .subscribe({
                    myUserInfo.value = Resource.success(userInfoLoggedIn.apply { sessionId = "" }) // Don't save the sessionId in the db because It's saved as ciphertext in the prefs
                }, {
                    myUserInfo.value = Resource.error(R.string.login_message_error_loggin_in)
                }))
    }

}