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

package com.sneyder.sdmessages.ui.search

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.*
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import debug
import io.reactivex.Flowable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        schedulerProvider: SchedulerProvider
): BaseViewModel(schedulerProvider) {

    val people: MutableLiveData<Resource<List<Contact>>> = MutableLiveData()

    fun searchPeopleByName(name: String) {
        add(Flowable.merge(userRepository.findUsersByName(name), userRepository.findGroupsByName(name))
                .applySchedulers()
                .filter { it.isNotEmpty() }
                .subscribeBy(
                        onNext = { it->
                            var groups: List<Contact> = ArrayList()
                            var friends: List<Contact> = ArrayList()
                            //if(resultPeople.isNotEmpty()) people.value = Resource.success(resultPeople)
                            when {
                                it.getOrNull(0) is UserInfo -> {
                                    friends = it.filterIsInstance(UserInfo::class.java).map { it.asContact() }
                                }
                                else -> {
                                    groups = it.filterIsInstance(GroupInfo::class.java).map { it.asContact() }
                                }
                            }
                            people.value = Resource.success((friends + groups).distinct())
                        },
                        onError = { people.value = Resource.error(R.string.search_people_message_error_loading_people) }
                ))
    }

    fun sendFriendRequest(userSelected: UserInfo, message: String) {
        debug("sendFriendRequest to $userSelected with message $message")
        add(userRepository.sendFriendRequest(userId = userRepository.getCurrentUserId(), sessionId = userRepository.getCurrentSessionId(),
                otherFirebaseTokenId = userSelected.firebaseTokenId?:"", otherUserId = userSelected.userId, message = message)
                .applySchedulers()
                .subscribeBy())
    }

}