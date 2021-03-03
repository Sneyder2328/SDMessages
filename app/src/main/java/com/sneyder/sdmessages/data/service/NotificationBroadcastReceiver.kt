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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sneyder.sdmessages.BaseApp
import android.widget.Toast
import com.sneyder.sdmessages.data.model.TypeContent
import com.sneyder.sdmessages.data.repository.MessageRepository
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.utils.*
import debug
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import notificationManager
import javax.inject.Inject


class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var userRepository: UserRepository

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as BaseApp).appComponent.inject(this)
        if (ACTION_REPLY_NEW_MESSAGE == intent.action) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            val message: String = getReplyMessage(intent).toString()
            val recipientId: String  = intent.getStringExtra(EXTRA_RECIPIENT_ID)
            Toast.makeText(context, "Message: $message to $recipientId", Toast.LENGTH_SHORT).show()
            context.notificationManager.cancel(NOTIFICATION_ID_NEW_MESSAGE)

            messageRepository.sendMessage(
                    senderId = userRepository.getCurrentUserId(),
                    sessionId = userRepository.getCurrentSessionId(),
                    recipientId = recipientId,
                    content = message,
                    typeContent = TypeContent.TEXT.data,
                    isRecipientAGroup = false
            )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = { debug("onSuccess sendMessage = $it") },
                            onError = { error("onError sendMessage = ${it.message}") }
                    )
        }
    }


}
