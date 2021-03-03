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

import com.sneyder.sdmessages.data.model.Message
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

abstract class MessageRepository {

    abstract fun findMessagesWithUserId(userId: String, sessionId: String, friendUserId: String): Flowable<List<Message>>

    abstract fun findMessagesWithGroupId(userId: String, sessionId: String, groupId: String): Flowable<List<Message>>

    abstract fun findAllMessages(): Flowable<List<Message>>

    abstract fun sendMessage(senderId: String, sessionId: String, recipientId: String, content: String, typeContent: String, isRecipientAGroup: Boolean): Single<String>

    abstract fun deleteMessageFromServer(userId: String, sessionId: String, friendUserId: String, lastMessageViewedDate: Long): Single<String>

    abstract fun saveMessage(message: Message): Completable

    abstract fun updateMessage(message: Message): Completable

    abstract fun updateMessagesWith1SecondMore(): Completable

    abstract fun deleteViewedMessages(): Completable

}