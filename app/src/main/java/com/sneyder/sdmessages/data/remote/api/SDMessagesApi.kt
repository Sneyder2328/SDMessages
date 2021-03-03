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
import com.sneyder.sdmessages.data.model.Message
import com.sneyder.sdmessages.data.model.UserInfo
import io.reactivex.Single
import retrofit2.http.*

interface SDMessagesApi {

    companion object {

        const val END_POINT = "https://sneyder.net/SDMessages/"

        const val SIGN_UP_USER = "signUp.php"

        const val LOG_IN_USER = "logIn.php"

        const val FIND_FRIENDS_BY_USER_ID = "findFriendsByUserId.php"

        const val FIND_GROUPS_BY_USER_ID = "findGroupsByUserId.php"

        const val FIND_USERS_BY_NAME = "findUsersByName.php"

        const val FIND_GROUPS_BY_NAME = "findGroupsByName.php"

        const val SEND_FRIEND_REQUEST = "sendFriendRequest.php"


        const val ACCEPT_FRIEND_REQUEST = "acceptFriendRequest.php"

        const val REJECT_FRIEND_REQUEST = "rejectFriendRequest.php"

        const val FIND_MESSAGES_WITH_USER_ID = "findMessagesWithUserId.php"

        const val FIND_MESSAGES_WITH_GROUP_ID = "findMessagesWithGroupId.php"

        const val SEND_MESSAGE_TO_FRIEND = "sendMessageToFriend.php"

        const val SEND_MESSAGE_TO_GROUP = "sendMessageToGroup.php"

        const val DELETE_MESSAGE_FROM_SERVER = "deleteMessageFromServer.php"

        const val FIND_USER_INFO_BY_ID = "findUserInfoById.php"

        const val UPDATE_USER_DISPLAY_NAME = "updateUserDisplayName.php"

        const val UPDATE_USER_PHOTO_URL = "updateUserPhotoUrl.php"

        const val UPDATE_FIREBASE_TOKENID = "updateFirebaseTokenId.php"

        const val LOG_OUT = "logOut.php"

        const val CREATE_NEW_GROUP = "createNewGroup.php"

    }

    @POST(SIGN_UP_USER)
    @FormUrlEncoded
    fun signUpUser(
            @Field("userId") userId: String,
            @Field("displayName") displayName: String,
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("typeLogin") typeLogin: String,
            @Field("birthDate") birthDate: String,
            @Field("firebaseTokenId") firebaseTokenId: String,
            @Field("photoUrl") photoUrl: String,
            @Field("accessToken") accessToken: String
    ): Single<UserInfo>

    @POST(LOG_IN_USER)
    @FormUrlEncoded
    fun logInUser(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("typeLogin") typeLogin: String,
            @Field("accessToken") accessToken: String = "",
            @Field("userId") userId: String = ""
    ): Single<UserInfo>

    @POST(FIND_USER_INFO_BY_ID)
    @FormUrlEncoded
    fun findUserInfoById(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String
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

    @GET
    fun findUsersByName(
            @Url url: String = FIND_USERS_BY_NAME,
            @Query("name") name: String
    ): Single<List<UserInfo>>

    @GET
    fun findGroupsByName(
            @Url url: String = FIND_GROUPS_BY_NAME,
            @Query("name") name: String
    ): Single<List<GroupInfo>>

    @POST(SEND_FRIEND_REQUEST)
    @FormUrlEncoded
    fun sendFriendRequest(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("otherFirebaseTokenId") otherFirebaseTokenId: String,
            @Field("otherUserId") otherUserId: String,
            @Field("message") message: String
    ): Single<String>

    @POST(UPDATE_FIREBASE_TOKENID)
    @FormUrlEncoded
    fun updateFirebaseTokenId(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("firebaseTokenId") firebaseTokenId: String
    ): Single<String>

    @POST(ACCEPT_FRIEND_REQUEST)
    @FormUrlEncoded
    fun acceptFriendRequest(
            @Field("fromUserId") fromUserId: String,
            @Field("toUserId") toUserId: String,
            @Field("sessionId") sessionId: String
    ): Single<String>

    @POST(REJECT_FRIEND_REQUEST)
    @FormUrlEncoded
    fun rejectFriendRequest(
            @Field("fromUserId") fromUserId: String,
            @Field("toUserId") toUserId: String,
            @Field("sessionId") sessionId: String
    ): Single<String>

    @GET
    fun findMessagesWithUserId(
            @Url url: String = FIND_MESSAGES_WITH_USER_ID,
            @Query("userId") userId: String,
            @Query("sessionId") sessionId: String,
            @Query("friendUserId") friendUserId: String
    ): Single<List<Message>>

    @GET
    fun findMessagesWithGroupId(
            @Url url: String = FIND_MESSAGES_WITH_GROUP_ID,
            @Query("userId") userId: String,
            @Query("sessionId") sessionId: String,
            @Query("groupId") groupId: String
    ): Single<List<Message>>

    @POST(SEND_MESSAGE_TO_FRIEND)
    @FormUrlEncoded
    fun sendMessageToFriend(
            @Field("senderId") senderId: String,
            @Field("sessionId") sessionId: String,
            @Field("recipientId") recipientId: String,
            @Field("content") content: String,
            @Field("typeContent") typeContent: String
    ): Single<String>

    @POST(SEND_MESSAGE_TO_GROUP)
    @FormUrlEncoded
    fun sendMessageToGroup(
            @Field("senderId") senderId: String,
            @Field("sessionId") sessionId: String,
            @Field("recipientGroupId") recipientGroupId: String,
            @Field("content") content: String,
            @Field("typeContent") typeContent: String
    ): Single<String>

    @POST(DELETE_MESSAGE_FROM_SERVER)
    @FormUrlEncoded
    fun deleteMessageFromServer(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("friendUserId") friendUserId: String,
            @Field("lastMessageViewedDate") lastMessageViewedDate: String
    ): Single<String>

    @POST(UPDATE_USER_DISPLAY_NAME)
    @FormUrlEncoded
    fun updateUserDisplayName(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("displayName") displayName: String
    ): Single<String>

    @POST(UPDATE_USER_PHOTO_URL)
    @FormUrlEncoded
    fun updateUserPhotoUrl(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("photoUrl") photoUrl: String
    ): Single<String>

    @POST(LOG_OUT)
    @FormUrlEncoded
    fun logOut(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String
    ): Single<String>

    @POST(CREATE_NEW_GROUP)
    @FormUrlEncoded
    fun createNewGroup(
            @Field("groupId") groupId: String,
            @Field("name") name: String,
            @Field("adminId") adminId: String,
            @Field("adminSessionId") adminSessionId: String,
            @Field("pictureUrl") pictureUrl: String,
            @Field("typeAccess") typeAccess: String,
            @Field("password") password: String
    ): Single<GroupInfo>

}