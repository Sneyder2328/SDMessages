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

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.aws.S3FilesManager
import com.sneyder.sdmessages.data.hashGenerator.Hasher
import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.data.model.UserRequest
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import javax.inject.Inject

class RegisterViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val s3FilesManager: S3FilesManager,
        private val hasher: Hasher,
        schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val userInfo: MutableLiveData<Resource<UserInfo>> = MutableLiveData()
    private var isThereAnImgBeingUploaded = false

    fun signUp(userRequest: UserRequest) {
        userInfo.value = Resource.loading()
        launch(UI) {
            add(userRepository.signUpUser(userRequest.apply { password = hasher.hash(password + email).await() })
                    .applySchedulers()
                    .subscribe({ userInfoResult: UserInfo ->
                        if (isThereAnImgBeingUploaded) userInfoSignedUp = userInfoResult
                        else insertUserSignedUpInDb(userInfoResult)
                    }, {
                        userInfo.value = Resource.error(R.string.register_message_error_singing_up)
                        s3FilesManager.cancelAll()
                    }))
        }
    }

    private var userInfoSignedUp: UserInfo? = null

    private fun insertUserSignedUpInDb(userInfoSignedUp: UserInfo) {
        add(userRepository.insertUser(userInfoSignedUp)
                .applySchedulers()
                .subscribe({
                    userInfo.value = Resource.success(userInfoSignedUp.apply { sessionId = "" }) // Don't save the sessionId in the db because It's saved as ciphertext in the prefs
                }, {
                    error("Very weird error inserting in user in the local database")
                }))
    }

    fun uploadImage(fileImg: File, key: String) {
        isThereAnImgBeingUploaded = true
        add(s3FilesManager.uploadFileCompressed(fileImg, key, 512f, 512f)
                .applySchedulers()
                .subscribeBy(
                        onComplete = { imgUploadFinished() },
                        onError = { imgUploadFinished() }
                ))
    }

    /**
     * if the singing up has not finished, mark [isThereAnImgBeingUploaded] as false
     * otherwise, [insertUserSignedUpInDb]
     * It works the same regardless of whether the upload finished successfully or not
     */
    private fun imgUploadFinished() {
        if (userInfoSignedUp == null) isThereAnImgBeingUploaded = false
        else insertUserSignedUpInDb(userInfoSignedUp!!)
    }
}