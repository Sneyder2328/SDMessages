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

package com.sneyder.sdmessages.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sneyder.sdmessages.BaseApp
import com.sneyder.sdmessages.data.model.FriendRequest
import com.sneyder.sdmessages.data.rxbus.OneShotLiveDataBus
import com.sneyder.sdmessages.utils.NOTIFICATION_ID_FRIEND_REQUESTS
import com.sneyder.sdmessages.utils.NOTIFICATION_ID_NEW_MESSAGE
import com.sneyder.sdmessages.utils.buildNewFriendRequestNotification
import com.sneyder.sdmessages.utils.buildNewMessageNotification
import debug
import notificationManager
import javax.inject.Inject

class AppFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var friendRequestsBus: OneShotLiveDataBus<FriendRequest>
    @Inject lateinit var newMessagesBus: OneShotLiveDataBus<NewMessageData>

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        debug("onMessageReceived ${remoteMessage?.data.toString()}")
        (application as BaseApp).appComponent.inject(this)

        remoteMessage?.apply {
            debug("from ${remoteMessage.from} messageType ${remoteMessage.messageType} to ${remoteMessage.to} sentTime ${remoteMessage.sentTime}")
            val notificationType = data["notificationType"] ?: return
            val fromUserId = data["fromUserId"] ?: return
            val fromUserName = data["fromUserName"] ?: return
            val fromPhotoUrl = data["fromPhotoUrl"] ?: ""
            val fromFirebaseTokenId = data["fromFirebaseTokenId"] ?: return
            val toUserId = data["toUserId"] ?: return

            when (notificationType) {
                NotificationType.FRIEND_REQUEST.data -> {
                    val message = data["message"] ?: ""
                    val friendRequest = FriendRequest(fromUserId, fromUserName, fromPhotoUrl, fromFirebaseTokenId, toUserId, message)
                    if (!friendRequestsBus.publish(message = friendRequest))
                        showFriendRequestNotification(friendRequest)
                }
                NotificationType.NEW_MESSAGE.data -> {
                    val content = data["content"] ?: ""
                    val typeContent = data["typeContent"] ?: ""
                    val newMessageData = NewMessageData(fromUserId, fromUserName, fromPhotoUrl, fromFirebaseTokenId, content, typeContent)
                    if (!newMessagesBus.publish(fromUserId, newMessageData))
                        showNewMessageNotification(newMessageData)
                }
            }
        }
    }

    private fun showFriendRequestNotification(friendRequest: FriendRequest) {
        debug("showFriendRequestNotification($friendRequest)")
        val notification = buildNewFriendRequestNotification(
                this,
                true,
                friendRequest
        )
        notificationManager.notify(NOTIFICATION_ID_FRIEND_REQUESTS, notification)
    }

    private fun showNewMessageNotification(newMessageData: NewMessageData) {
        debug("showNewMessageNotification $newMessageData")
        val notification = buildNewMessageNotification(
                this,
                true,
                newMessageData)
        notificationManager.notify(NOTIFICATION_ID_NEW_MESSAGE, notification)
    }

    enum class NotificationType(val data: String) {
        FRIEND_REQUEST("friendRequest"),
        NEW_MESSAGE("newMessage")
    }

    data class NewMessageData(var fromUserId: String, var fromUserName: String, var fromPhotoUrl: String, var fromFirebaseTokenId: String, var content: String, var typeContent: String)
}