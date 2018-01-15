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

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.data.aws.S3FilesManager
import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import com.sneyder.sdmessages.utils.SingleLiveEvent
import com.sneyder.sdmessages.utils.generateKeyForS3
import com.sneyder.sdmessages.utils.getS3BucketWithKey
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import javax.inject.Inject

class ProfileViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val s3FilesManager: S3FilesManager,
        schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val myUserInfo: MutableLiveData<Resource<UserInfo>> = MutableLiveData()

    fun loadUserInfo() {
        add(userRepository.findMyUserInfo()
                .applySchedulers()
                .subscribeBy(
                        onError = { myUserInfo.value = Resource.error() },
                        onNext = { myUserInfo.value = Resource.success(it) }
                ))
    }

    val updateUsername: SingleLiveEvent<Resource<String>> = SingleLiveEvent()

    fun updateUserName(userName: String) {
        updateUsername.value = Resource.loading()
        add(userRepository.updateUserDisplayName(userRepository.getCurrentUserId(), userRepository.getCurrentSessionId(), userName)
                .applySchedulers()
                .subscribeBy(
                        onError = { updateUsername.value = Resource.error() },
                        onSuccess = { updateUsername.value = Resource.success(userName) }
                ))
    }

    private var isThereAnImgBeingUploaded = false
    val imgUploading: SingleLiveEvent<Resource<String>> = SingleLiveEvent()

    fun uploadImage(fileImg: File) {
        if (isThereAnImgBeingUploaded) return
        isThereAnImgBeingUploaded = true
        val key = generateKeyForS3()
        val bucketWithKey = getS3BucketWithKey(key)
        add(s3FilesManager.uploadFileCompressed(fileImg, key, 512f, 512f)
                .applySchedulers()
                .subscribeBy(
                        onComplete = {
                            isThereAnImgBeingUploaded = false
                            updatePhotoUrl(bucketWithKey)
                            imgUploading.value = Resource.success(bucketWithKey)
                        },
                        onNext = { imgUploading.value = Resource.loading() },
                        onError = {
                            isThereAnImgBeingUploaded = false
                            imgUploading.value = Resource.error()
                        }
                ))
    }

    private fun updatePhotoUrl(photoUrl: String) {
        add(userRepository.updateUserPhotoUrl(userRepository.getCurrentUserId(), userRepository.getCurrentSessionId(), photoUrl)
                .applySchedulers()
                .subscribeBy(
                        onError = {},
                        onSuccess = {}
                ))
    }
}