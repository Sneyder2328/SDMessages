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

import com.sneyder.sdmessages.data.local.database.GroupDao
import com.sneyder.sdmessages.data.local.database.UserDao
import com.sneyder.sdmessages.data.local.preferences.PreferencesHelper
import com.sneyder.sdmessages.data.model.GroupInfo
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.data.model.UserRequest
import com.sneyder.sdmessages.data.remote.api.SDMessagesApi
import debug
import error
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class AppUserRepository
@Inject constructor(
        private val sdMessagesApi: SDMessagesApi,
        private val userDao: UserDao,
        private val groupDao: GroupDao,
        preferencesHelper: PreferencesHelper
): UserRepository(preferencesHelper) {

    override fun insertUser(userInfo: UserInfo): Completable {
        return Completable.fromAction { userDao.insert(userInfo) }
                .doOnComplete { debug("doOnComplete insertUser($userInfo)") }
                .doOnError { error("doOnError insertUser($userInfo)") }
    }

    override fun signUpUser(user: UserRequest): Single<UserInfo> {
        return sdMessagesApi.signUpUser(userId = user.userId, email = user.email, birthDate = user.birthDate,
                displayName = user.displayName, photoUrl = user.photoUrl, typeLogin = user.typeLogin, password = user.password, accessToken = user.accessToken)
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
        return userDao.findUserById(getCurrentUserId())
                .distinctUntilChanged()
                .doOnNext { debug("doOnNext findMyUserInfo() = $it") }
                .doOnError { error("doOnError findMyUserInfo() = ${it.message}") }
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    override fun findMyFriends(): Flowable<List<UserInfo>> {
        compositeDisposable.add(sdMessagesApi.findFriendsByUserId(userId = getCurrentUserId(), sessionId = getCurrentSessionId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            debug("doOnNext online findMyFriends() = $it")
                            userDao.insertUsers(*it.toTypedArray())
                        },
                        onError = { error("doOnError online findMyFriends() = ${it.message}") }
                ))
        return userDao.findUsersByNotId(getCurrentUserId())
                .distinctUntilChanged()
                .doOnNext { debug("doOnNext offline findMyFriends() = $it") }
                .doOnError { error("doOnError offline findMyFriends() = ${it.message}") }
    }

    override fun findMyGroups(): Flowable<List<GroupInfo>> {
        compositeDisposable.add(sdMessagesApi.findGroupsByUserId(userId = getCurrentUserId(), sessionId = getCurrentSessionId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        debug("doOnNext online findMyGroups() = $it")
                        groupDao.insertGroups(*it.toTypedArray())
                    },
                    onError = { error("doOnError online findMyGroups() = ${it.message}") }
                ))

        return groupDao.findGroups()
                .distinctUntilChanged()
                .doOnNext { debug("doOnNext offline findMyGroups() = $it") }
                .doOnError { error("doOnError offline findMyGroups() = ${it.message}") }
    }
}