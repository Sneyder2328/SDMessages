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
import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ChatsViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val friends: MutableLiveData<Resource<List<UserInfo>>> = MutableLiveData()

    private var firstLoad = true

    fun loadChats(forceUpdate: Boolean = false): Boolean {
        if (!firstLoad && !forceUpdate) return false
        return add(userRepository.findMyFriends()
                .applySchedulers()
                .subscribeBy(
                        onNext = { resultFriends->
                            if (resultFriends.isNotEmpty()) friends.value = Resource.success(resultFriends)
                        },
                        onError = { friends.value = Resource.error(R.string.chats_message_error_loading_chats) }
                )).also { firstLoad = false }
    }

}