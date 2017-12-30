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

package com.sneyder.sdmessages.data.remote.api

import com.sneyder.sdmessages.data.model.GroupInfo
import com.sneyder.sdmessages.data.model.UserInfo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface SDMessagesApi {

    companion object {

        const val END_POINT = "https://sneyder.net/SDMessages/"

        const val SIGN_UP_USER = "signUp.php"

        const val LOG_IN_USER = "logIn.php"

        const val FIND_FRIENDS_BY_USER_ID = "findFriendsByUserId.php"

        const val FIND_GROUPS_BY_USER_ID = "findGroupsByUserId.php"

    }

    @POST
    fun signUpUser(
            @Url url: String = SIGN_UP_USER,
            @Query("userId") userId: String,
            @Query("displayName") displayName: String,
            @Query("email") email: String,
            @Query("password") password: String,
            @Query("typeLogin") typeLogin: String,
            @Query("birthDate") birthDate: String,
            @Query("photoUrl") photoUrl: String,
            @Query("accessToken") accessToken: String
    ): Single<UserInfo>

    @POST
    fun logInUser(
            @Url url: String = LOG_IN_USER,
            @Query("email") email: String,
            @Query("password") password: String,
            @Query("typeLogin") typeLogin: String,
            @Query("accessToken") accessToken: String = "",
            @Query("userId") userId: String = ""
    ): Single<UserInfo>

    @GET
    fun findFriendsByUserId(
            @Url url: String = FIND_FRIENDS_BY_USER_ID,
            @Query("userId") userId: String,
            @Query("sessionId") sessionId: String
    ): Single<List<UserInfo>>

    @GET
    fun findGroupsByUserId(
            @Url url: String = FIND_GROUPS_BY_USER_ID,
            @Query("userId") userId: String,
            @Query("sessionId") sessionId: String
    ): Single<List<GroupInfo>>

}