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

package com.sneyder.sdmessages.ui.main.new_group

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.aws.S3FilesManager
import com.sneyder.sdmessages.data.model.GroupInfo
import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import com.sneyder.sdmessages.utils.SingleLiveEvent
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import generateUUID
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import javax.inject.Inject

class NewGroupViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val s3FilesManager: S3FilesManager,
        schedulerProvider: SchedulerProvider)
    : BaseViewModel(schedulerProvider) {

    val groupInfo: MutableLiveData<Resource<GroupInfo>> = MutableLiveData()

    fun createGroup(groupName: String, pictureUrl: String){
        groupInfo.value = Resource.loading()
        add(userRepository.createNewGroup(groupId = generateUUID(), name = groupName, pictureUrl = pictureUrl)
                .applySchedulers()
                .subscribeBy(
                        onSuccess = { groupInfoResult ->
                            if (isThereAnImgBeingUploaded) groupInfoCreated  = groupInfoResult
                            else insertGroupCreatedInDb(groupInfoResult)
                        },
                        onError = {
                            groupInfo.value = Resource.error(R.string.new_group_message_error_creating_group)
                            s3FilesManager.cancelAll()
                        }
                ))
    }

    private var isThereAnImgBeingUploaded = false
    val imgUploading: SingleLiveEvent<Resource<Int>> = SingleLiveEvent()

    private var groupInfoCreated: GroupInfo? = null

    private fun insertGroupCreatedInDb(groupInfoCreated: GroupInfo) {
        add(userRepository.insertGroup(groupInfoCreated)
                .applySchedulers()
                .subscribe({
                    groupInfo.value = Resource.success(groupInfoCreated)
                }, {
                    error("Very weird error inserting group in the local database")
                }))
    }

    fun uploadImage(fileImg: File, key: String) {
        if (isThereAnImgBeingUploaded) return
        isThereAnImgBeingUploaded = true
        add(s3FilesManager.uploadFileCompressed(fileImg, key, 512f, 512f)
                .applySchedulers()
                .subscribeBy(
                        onComplete = {
                            imgUploading.value = Resource.success(100)
                            imgUploadFinished()
                        },
                        onNext = { imgUploading.value = Resource.loading(it) },
                        onError = {
                            imgUploading.value = Resource.error()
                            imgUploadFinished()
                        }
                ))
    }

    private fun imgUploadFinished() {
        if (groupInfoCreated == null) isThereAnImgBeingUploaded = false
        else insertGroupCreatedInDb(groupInfoCreated!!)
    }

}