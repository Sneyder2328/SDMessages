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

package com.sneyder.sdmessages.ui.main

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.FriendRequest
import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import com.sneyder.sdmessages.ui.base.BaseViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MainViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        schedulersProvider: SchedulerProvider
): BaseViewModel(schedulersProvider) {

    fun keepFirebaseTokenIdUpdated(token: String?): Boolean {
        if (token == null) return false
        add(userRepository.updateFirebaseTokenId(userRepository.getCurrentUserId(), userRepository.getCurrentSessionId(), token)
                .applySchedulers()
                .subscribeBy(
                        onSuccess = {  },
                        onError = {  }
                ))
        return true
    }

    val incomingFriendRequest: MutableLiveData<Resource<Any>> = MutableLiveData()

    fun acceptFriendRequest(pendingFriendRequest: FriendRequest?) {
        with(pendingFriendRequest ?: return){
            add(userRepository.acceptFriendRequest(fromUserId = fromUserId, toUserId = toUserId, sessionId = userRepository.getCurrentSessionId())
                    .applySchedulers()
                    .subscribeBy(
                            onSuccess = { incomingFriendRequest.value = Resource.success(it) },
                            onError = {  }
                    ))
        }
    }

    fun rejectFriendRequest(pendingFriendRequest: FriendRequest?) {
        with(pendingFriendRequest ?: return){
            add(userRepository.rejectFriendRequest(fromUserId = fromUserId, toUserId = toUserId, sessionId = userRepository.getCurrentSessionId())
                    .applySchedulers()
                    .subscribeBy(
                            onSuccess = {  },
                            onError = {  }
                    ))
        }
    }

    val loggedOut: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    fun logOut() {
        add(userRepository.logOut()
                .applySchedulers()
                .subscribeBy(
                        onSuccess = { loggedOut.value = Resource.success(true) },
                        onError = { loggedOut.value = Resource.error(R.string.main_error_logging_out) }
                )
        )
    }

}