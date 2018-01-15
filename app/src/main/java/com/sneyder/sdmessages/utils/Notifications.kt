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

package com.sneyder.sdmessages.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import android.support.v4.content.ContextCompat
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.FriendRequest
import com.sneyder.sdmessages.data.model.TypeContent
import com.sneyder.sdmessages.data.service.AppFirebaseMessagingService
import com.sneyder.sdmessages.ui.main.MainActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import toBitmap
import android.content.Intent
import com.sneyder.sdmessages.data.service.NotificationBroadcastReceiver
import com.sneyder.sdmessages.ui.conversation.ConversationActivity
import debug
import into
import isNougatOrLater
import load


const val NOTIFICATION_ID_FRIEND_REQUESTS = 2
const val ID_CHANNEL_FRIEND_REQUESTS = "FriendRequestsChannel"
const val NAME_CHANNEL_FRIEND_REQUESTS= "Friend requests"

const val NOTIFICATION_ID_NEW_MESSAGE = 3
const val ID_CHANNEL_NEW_MESSAGE = "NewMessagesChannel"
const val NAME_CHANNEL_NEW_MESSAGE = "New messages"

const val KEY_REPLY = "ReplyToNewMessage"
const val EXTRA_RECIPIENT_ID = "recipientId"
const val ACTION_REPLY_NEW_MESSAGE = "replyNewMessage"

fun buildNewFriendRequestNotification(
        context: Context,
        vibrate: Boolean = false,
        friendRequest: FriendRequest
): Notification = NotificationCompat.Builder(context, ID_CHANNEL_FRIEND_REQUESTS).apply {
    setSmallIcon(R.drawable.ic_notification_friend_request)
    priority = NotificationCompat.PRIORITY_HIGH
    setWhen(System.currentTimeMillis())
    setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    setContentTitle(context.getString(R.string.notification_title_friend_request))
    setContentText(String.format(context.getString(R.string.notification_content_friend_reques), friendRequest.fromUserName))
    if (vibrate) setVibrate(longArrayOf(0, 300, 700, 300))
    setContentIntent(PendingIntent.getActivity(
            context,
            0,
            MainActivity.starterIntentWithFriendRequest(context.applicationContext, friendRequest),
            PendingIntent.FLAG_ONE_SHOT))
    setAutoCancel(true)
    addAction(createAcceptFriendRequestAction(context, friendRequest))
    addAction(createRejectFriendRequestAction(context, friendRequest))
}.build()

fun createAcceptFriendRequestAction(context: Context, friendRequest: FriendRequest)
        : NotificationCompat.Action {
    val acceptRequestPendingIntent = PendingIntent.getActivity(
            context,
            1,
            MainActivity.starterIntentWithFriendRequest(context, friendRequest).setAction(Action.ACCEPT_FRIEND_REQUEST),
            PendingIntent.FLAG_ONE_SHOT)

    return NotificationCompat.Action.Builder(
            R.drawable.ic_notification_friend_request,
            context.getString(R.string.notification_action_accept_friend_request),
            acceptRequestPendingIntent)
            .build()
}

fun createRejectFriendRequestAction(context: Context, friendRequest: FriendRequest)
        : NotificationCompat.Action {
    val acceptRequestPendingIntent = PendingIntent.getActivity(
            context,
            2,
            MainActivity.starterIntentWithFriendRequest(context, friendRequest).setAction(Action.REJECT_FRIEND_REQUEST),
            PendingIntent.FLAG_ONE_SHOT)

    return NotificationCompat.Action.Builder(
            R.drawable.ic_notification_friend_request,
            context.getString(R.string.notification_action_reject_friend_request),
            acceptRequestPendingIntent).build()
}

fun buildNewMessageNotification(
        context: Context,
        vibrate: Boolean = false,
        newMessageData: AppFirebaseMessagingService.NewMessageData

): Notification = NotificationCompat.Builder(context, ID_CHANNEL_NEW_MESSAGE).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    context.load(newMessageData.fromPhotoUrl).into(object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        override fun onBitmapFailed(errorDrawable: Drawable?) {
            setLargeIcon(ContextCompat.getDrawable(context, R.drawable.ic_user_img_profile)?.toBitmap())
        }
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            bitmap?.let { setLargeIcon(it) }
        }
    })
    priority = NotificationCompat.PRIORITY_HIGH
    setWhen(System.currentTimeMillis())
    setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    setContentTitle(newMessageData.fromUserName)

    if(newMessageData.typeContent == TypeContent.TEXT.data) {
        setContentText(newMessageData.content)
    }
    else if(newMessageData.typeContent == TypeContent.IMAGE.data) {
        context.load(newMessageData.content).into(object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    debug("newMessageData.content onPrepareLoad")
                }
                override fun onBitmapFailed(errorDrawable: Drawable?) {}
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    debug("newMessageData.content onBitmapLoaded")
                    bitmap?.let { setStyle(NotificationCompat.BigPictureStyle().bigPicture(it)) }
                }
        })
    }
    if (vibrate) setVibrate(longArrayOf(0, 300, 700, 300))
    setContentIntent(PendingIntent.getActivity(
            context,
            0,
            ConversationActivity.starterIntent(context, newMessageData),
            PendingIntent.FLAG_ONE_SHOT))
    setAutoCancel(true)
    addAction(createReplyAction(context, newMessageData))
}.build()

private fun createReplyAction(
        context: Context,
        newMessageData: AppFirebaseMessagingService.NewMessageData
): NotificationCompat.Action {
    val remoteInput = RemoteInput.Builder(KEY_REPLY)
            .setLabel(context.getString(R.string.notification_hint_type_reply))
            .build()
    return NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_get,
            "Reply",
            getMessageReplyIntent(context, newMessageData))
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
    .build()
}

fun getMessageReplyIntent(
        context: Context,
        newMessageData: AppFirebaseMessagingService.NewMessageData
): PendingIntent {
    return if (isNougatOrLater()) { // send the message through a broadcast receiver in the background in Api Nougat or above
        val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            action = ACTION_REPLY_NEW_MESSAGE
            putExtra(EXTRA_RECIPIENT_ID, newMessageData.fromUserId)
        }
        PendingIntent.getBroadcast(context.applicationContext, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    } else {
        // in lower apis start ConversationActivity with the new
        PendingIntent.getActivity(context,
                100,
                ConversationActivity.starterIntent(context, newMessageData).apply { action = ACTION_REPLY_NEW_MESSAGE },
                PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

fun getReplyMessage(intent: Intent): CharSequence? {
    val remoteInput = RemoteInput.getResultsFromIntent(intent)
    return remoteInput?.getCharSequence(KEY_REPLY)
}
