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

package com.sneyder.sdmessages.data.repository

import com.sneyder.sdmessages.data.local.preferences.PreferencesHelper
import com.sneyder.sdmessages.data.model.GroupInfo
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.data.model.UserRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

abstract class UserRepository(
        private val preferencesHelper: PreferencesHelper
): PreferencesHelper by preferencesHelper {

    abstract fun insertUser(userInfo: UserInfo): Completable

    abstract fun insertGroup(groupInfo: GroupInfo): Completable

    abstract fun logInUser(user: UserRequest): Single<UserInfo>

    abstract fun signUpUser(user: UserRequest): Single<UserInfo>

    abstract fun findMyUserInfo(): Flowable<UserInfo>

    abstract fun findMyFriends(): Flowable<List<UserInfo>>

    abstract fun findMyGroups(): Flowable<List<GroupInfo>>

    abstract fun findUsersByName(name: String): Flowable<List<UserInfo>>

    abstract fun findGroupsByName(name: String): Flowable<List<GroupInfo>>

    abstract fun sendFriendRequest(userId: String, sessionId: String, otherFirebaseTokenId: String, otherUserId: String, message: String): Single<String>

    abstract fun updateFirebaseTokenId(userId: String, sessionId: String, firebaseTokenId: String): Single<String>

    abstract fun updateUserPhotoUrl(userId: String, sessionId: String, photoUrl: String): Single<String>

    abstract fun updateUserDisplayName(userId: String, sessionId: String, displayName: String): Single<String>

    abstract fun acceptFriendRequest(fromUserId: String, toUserId: String, sessionId: String): Single<String>

    abstract fun rejectFriendRequest(fromUserId: String, toUserId: String, sessionId: String): Single<String>

    abstract fun createNewGroup(groupId: String, name: String, pictureUrl: String): Single<GroupInfo>

    abstract fun logOut(): Single<String>

}