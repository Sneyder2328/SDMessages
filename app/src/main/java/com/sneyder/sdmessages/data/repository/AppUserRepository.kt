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
import com.sneyder.sdmessages.data.local.database.AppDatabase
import com.sneyder.sdmessages.data.local.database.GroupDao
import com.sneyder.sdmessages.data.local.database.UserDao
import com.sneyder.sdmessages.data.local.preferences.PreferencesHelper
import com.sneyder.sdmessages.data.model.*
import com.sneyder.sdmessages.data.remote.api.SDMessagesApi
import debug
import error
import io.reactivex.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppUserRepository
@Inject constructor(
        private val sdMessagesApi: SDMessagesApi,
        private val appDatabase: AppDatabase,
        private val userDao: UserDao,
        private val groupDao: GroupDao,
        preferencesHelper: PreferencesHelper
) : UserRepository(preferencesHelper) {

    override fun insertUser(userInfo: UserInfo): Completable {
        return Completable.fromAction { userDao.insert(checkIfMyself(userInfo)) }
                .doOnComplete { debug("doOnComplete insertUser($userInfo)") }
                .doOnError { error("doOnError insertUser($userInfo)") }
    }

    private fun checkIfMyself(userInfo: UserInfo): UserInfo {
        return if (userInfo.userId == getCurrentUserId()) userInfo.apply { typeUser = TypeUser.MYSELF.data } else userInfo
    }

    override fun signUpUser(user: UserRequest): Single<UserInfo> {
        return sdMessagesApi.signUpUser(userId = user.userId, email = user.email, birthDate = user.birthDate, displayName = user.displayName,
                photoUrl = user.photoUrl, typeLogin = user.typeLogin, password = user.password, firebaseTokenId = user.firebaseTokenId, accessToken = user.accessToken)
                .doOnSuccess {
                    debug("doOnSuccess signUpUser($user) = $it")
                    setLogged(true)
                    setCurrentUserId(it.userId)
                    setCurrentSessionId(it.sessionId ?: return@doOnSuccess)
                }
                .doOnError { error("doOnError signUpUser($user) = ${it.message}") }
    }

    override fun logInUser(user: UserRequest): Single<UserInfo> {
        return sdMessagesApi.logInUser(email = user.email, typeLogin = user.typeLogin, password = user.password, userId = user.userId, accessToken = user.accessToken)
                .doOnSuccess {
                    debug("doOnSuccess logInUser($user) = $it")
                    setLogged(true)
                    setCurrentUserId(it.userId)
                    setCurrentSessionId(it.sessionId ?: return@doOnSuccess)
                }
                .doOnError {
                    it.printStackTrace()
                    error("doOnError logInUser($user) = ${it.message}")
                }
    }

    override fun findMyUserInfo(): Flowable<UserInfo> {
        val apiCall = sdMessagesApi.findUserInfoById(userId = getCurrentUserId(), sessionId = getCurrentSessionId())
                .doOnSuccess {
                    it.typeUser = TypeUser.MYSELF.data
                    debug("doOnSuccess online findMyUserInfo() = $it")
                    userDao.insert(it)
                }
                .doOnError { error("doOnError online findMyUserInfo() = ${it.message}") }
                .onErrorReturn {
                    userDao.findUserById(getCurrentUserId()).blockingFirst()
                }
                .toFlowable()

        return Flowable.merge(userDao.findUserById(getCurrentUserId()), apiCall)
                .distinctUntilChanged()
                .doOnNext { debug("doOnNext findMyUserInfo() = $it") }
                .doOnError { error("doOnError findMyUserInfo() = ${it.message}") }
    }

    override fun findMyFriends(): Flowable<List<UserInfo>> {
        val apiCall = sdMessagesApi.findFriendsByUserId(userId = getCurrentUserId(), sessionId = getCurrentSessionId())
                .doOnSuccess { friends->
                    debug("doOnSuccess online findMyFriends() = $friends")
                    friends.forEach { it.typeUser = TypeUser.FRIEND.data }
                    userDao.insertUsers(*friends.toTypedArray())
                }
                .doOnError{ error("doOnError online findMyFriends() = ${it.message}") }
                .onErrorReturn { emptyList() }
                .toFlowable()

        return Flowable.merge(userDao.findFriends(), apiCall)
                .doOnNext { debug("doOnNext findMyFriends() = $it") }
                .doOnError { error("doOnError findMyFriends() = ${it.message}") }
    }

    override fun findMyGroups(): Flowable<List<GroupInfo>> {
        val apiCall = sdMessagesApi.findGroupsByUserId(userId = getCurrentUserId(), sessionId = getCurrentSessionId())
                .doOnSuccess { groups->
                    debug("doOnSuccess online findMyGroups() = $groups")
                    groupDao.insertGroups(*groups.toTypedArray())
                }
                .doOnError { error("doOnError online findMyGroups() = ${it.message}") }
                .onErrorReturn { emptyList() }
                .toFlowable()

        return Flowable.merge(groupDao.findGroups(), apiCall)
                .doOnNext { debug("doOnNext findMyGroups() = $it") }
                .doOnError { error("doOnError findMyGroups() = ${it.message}") }
    }

    override fun findUsersByName(name: String): Flowable<List<UserInfo>> {
        val apiCall = if (name.isNotBlank()) {
            sdMessagesApi.findUsersByName(username = name)
                    .doOnSuccess { users ->
                        debug("doOnSuccess online findUsersByName($name) = $users")
                        if (users.isNotEmpty()) {
                            users.map { checkIfMyself(it) }
                            userDao.insertUsers(*users.toTypedArray())
                        }
                    }
                    .doOnError { error("doOnError online findUsersByName($name) = ${it.message}") }
                    .onErrorReturn { emptyList() }
                    .toFlowable()
        } else { Flowable.empty() }

        return Flowable.merge(userDao.findUsersByName(name), apiCall)
                .doOnNext { debug("doOnNext findUsersByName($name) = $it") }
                .doOnError { error("doOnError findUsersByName($name) = ${it.message}") }
    }


    override fun sendFriendRequest(userId: String, sessionId: String, otherFirebaseTokenId: String, otherUserId: String, message: String): Single<String> {
        return sdMessagesApi.sendFriendRequest(userId = userId, sessionId = sessionId, otherFirebaseTokenId = otherFirebaseTokenId, otherUserId = otherUserId, message = message)
                .doOnSuccess { debug("doOnSuccess sendFriendRequest(userId = $userId, sessionId = $sessionId, " +
                        "otherFirebaseTokenId = $otherFirebaseTokenId, otherUserId = $otherUserId, message = $message) = $it") }
                .doOnError { error("doOnError sendFriendRequest($userId, $sessionId, $otherFirebaseTokenId, $otherUserId, $message) = ${it.message}") }
    }

    override fun updateFirebaseTokenId(userId: String, sessionId: String, firebaseTokenId: String): Single<String> {
        return sdMessagesApi.updateFirebaseTokenId(userId = userId, sessionId = sessionId, firebaseTokenId = firebaseTokenId)
                .doOnSuccess { debug("doOnSuccess updateFirebaseTokenId() = $it") }
                .doOnError { error("doOnError updateFirebaseTokenId() = ${it.message}") }
    }

    override fun updateUserPhotoUrl(userId: String, sessionId: String, photoUrl: String): Single<String> {
        return sdMessagesApi.updateUserPhotoUrl(userId, sessionId, photoUrl)
                .doOnSuccess {
                    val myUser = userDao.findUserById(getCurrentUserId()).blockingFirst()
                    myUser.photoUrl = photoUrl
                    userDao.insert(myUser)
                    debug("doOnSuccess updateUserPhotoUrl($userId, $sessionId, $photoUrl) = $it")
                }
                .doOnError { error("doOnError updateUserPhotoUrl($userId, $sessionId, $photoUrl) = ${it.message}") }
    }

    override fun updateUserDisplayName(userId: String, sessionId: String, displayName: String): Single<String> {
        return sdMessagesApi.updateUserDisplayName(userId, sessionId, displayName)
                .doOnSuccess {
                    val myUser = userDao.findUserById(getCurrentUserId()).blockingFirst()
                    myUser.displayName = displayName
                    userDao.insert(myUser)
                    debug("doOnSuccess updateUserDisplayName($userId, $sessionId, $displayName) = $it")
                }
                .doOnError { error("doOnError updateUserDisplayName($userId, $sessionId, $displayName) = ${it.message}") }
    }

    override fun acceptFriendRequest(fromUserId: String, toUserId: String, sessionId: String): Single<String> {
        return sdMessagesApi.acceptFriendRequest(fromUserId = fromUserId, toUserId = toUserId, sessionId = sessionId)
                .doOnSuccess {
                    debug("doOnSuccess acceptFriendRequest(fromUserId = $fromUserId, toUserId = $toUserId, sessionId = $sessionId) = $it")
                    if(it != "true") throw Exception(it)
                }
                .doOnError { error("doOnError acceptFriendRequest(fromUserId = $fromUserId, toUserId = $toUserId, sessionId = $sessionId) = ${it.message}") }
    }

    override fun rejectFriendRequest(fromUserId: String, toUserId: String, sessionId: String): Single<String> {
        return sdMessagesApi.rejectFriendRequest(fromUserId = fromUserId, toUserId = toUserId, sessionId = sessionId)
                .doOnSuccess { debug("doOnSuccess rejectFriendRequest(fromUserId = $fromUserId, toUserId = $toUserId, sessionId = $sessionId) = $it") }
                .doOnError { error("doOnError rejectFriendRequest(fromUserId = $fromUserId, toUserId = $toUserId, sessionId = $sessionId) = ${it.message}") }
    }

    override fun logOut(): Single<String> {
        return sdMessagesApi.logOut(getCurrentUserId(), getCurrentSessionId())
                .doOnSuccess {
                    debug("doOnSuccess logOut() = $it")
                    logOutLocally()
                }
                .doOnError {
                    error("doOnError logOut() = ${it.message}")
                    logOutLocally()
                }
    }

    private fun logOutLocally(){
        clearPreferences()
        appDatabase.clearDatabase()
    }
}