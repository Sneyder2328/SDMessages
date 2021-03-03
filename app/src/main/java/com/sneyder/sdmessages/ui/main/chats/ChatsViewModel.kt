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

package com.sneyder.sdmessages.ui.main.chats

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.*
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import io.reactivex.Flowable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChatsViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val chatsLiveData: MutableLiveData<Resource<List<Contact?>>> = MutableLiveData()
    private var groups: List<Contact> = ArrayList()
    private var friends: List<Contact> = ArrayList()

    private var firstLoad = true

    fun loadChats(forceUpdate: Boolean = false): Boolean {
        if (!firstLoad && !forceUpdate) return false
        return add(Flowable.merge(userRepository.findMyFriends().subscribeOn(Schedulers.newThread()), userRepository.findMyGroups().subscribeOn(Schedulers.newThread()))
                .applySchedulers()
                .filter { it.isNotEmpty() }
                .subscribeBy(
                        onNext = { list->
                            when {
                                list.getOrNull(0) is UserInfo -> {
                                    friends = list.filterIsInstance(UserInfo::class.java).map { it.asContact() }
                                }
                                else -> {
                                    groups = list.filterIsInstance(GroupInfo::class.java).map { it.asContact() }
                                }
                            }
                            chatsLiveData.value = Resource.success((friends + groups).distinct())
                        },
                        onError = { chatsLiveData.value = Resource.error(R.string.chats_message_error_loading_chats) }
                )).also { firstLoad = false }
    }

}