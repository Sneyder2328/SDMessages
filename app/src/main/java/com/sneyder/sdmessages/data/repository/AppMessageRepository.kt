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

import com.sneyder.sdmessages.data.local.database.MessageDao
import com.sneyder.sdmessages.data.local.preferences.PreferencesHelper
import com.sneyder.sdmessages.data.model.Message
import com.sneyder.sdmessages.data.remote.api.SDMessagesApi
import debug
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMessageRepository
@Inject constructor(
        private val sdMessagesApi: SDMessagesApi,
        private val messageDao: MessageDao,
        private val preferencesHelper: PreferencesHelper
) : MessageRepository() {

    override fun findMessagesWithUserId(userId: String, sessionId: String, friendUserId: String): Flowable<List<Message>> {
        val apiCall = sdMessagesApi.findMessagesWithUserId(userId = userId, sessionId = sessionId, friendUserId = friendUserId)
                .doOnSuccess { messages ->
                    debug("doOnSuccess online findMessagesWithUserId(userId = $userId, sessionId = $sessionId, friendUserId = $friendUserId) = $messages")
                    messages.map { it.received = (it.recipientId == preferencesHelper.getCurrentUserId()) }
                    messageDao.insertMessages(*messages.toTypedArray())
                }
                .doOnError { error("doOnError online findMessagesWithUserId(userId = $userId, sessionId = $sessionId, friendUserId = $friendUserId) = ${it.message}") }
                .onErrorReturn {
                    debug("doOnError online findMessagesWithUserId return local db")
                    messageDao.findMessagesWithUserId(userId, friendUserId).blockingFirst()
                }
                .toFlowable()

        return Flowable.merge(messageDao.findMessagesWithUserId(userId, friendUserId), apiCall)
                .doOnNext { debug("doOnNext findMessagesWithUserId($userId, $friendUserId) = $it") }
                .doOnError { error("doOnError findMessagesWithUserId($userId, $friendUserId) = ${it.message}") }
                .doOnComplete { debug("doOnComplete findMessagesWithUserId") }
    }

    override fun findMessagesWithGroupId(userId: String, sessionId: String, groupId: String): Flowable<List<Message>> {
        val apiCall = sdMessagesApi.findMessagesWithGroupId(userId = userId, sessionId = sessionId, groupId = groupId)
                .doOnSuccess { messages ->
                    debug("doOnSuccess online findMessagesWithUserId(userId = $userId, sessionId = $sessionId, groupId = $groupId) = $messages")
                    messages.map { it.received = (it.recipientId == preferencesHelper.getCurrentUserId()) }
                    messageDao.insertMessages(*messages.toTypedArray())
                }
                .doOnError { error("doOnError online findMessagesWithUserId(userId = $userId, sessionId = $sessionId, groupId = $groupId) = ${it.message}") }
                .onErrorReturn {
                    debug("doOnError online findMessagesWithUserId return local db")
                    messageDao.findMessagesWithUserId(userId, groupId).blockingFirst()
                }
                .toFlowable()

        return Flowable.merge(messageDao.findMessagesWithUserId(userId, groupId), apiCall)
                .doOnNext { debug("doOnNext findMessagesWithUserId($userId, $groupId) = $it") }
                .doOnError { error("doOnError findMessagesWithUserId($userId, $groupId) = ${it.message}") }
                .doOnComplete { debug("doOnComplete findMessagesWithUserId") }
    }

    override fun sendMessage(senderId: String, sessionId: String, recipientId: String, content: String, typeContent: String, isRecipientAGroup: Boolean): Single<String> {
        debug("sendMessage($senderId: String, $sessionId: String, $recipientId: String, $content: String, $typeContent: String, $isRecipientAGroup)")
        val sendMessage = if (isRecipientAGroup) {
            sdMessagesApi.sendMessageToGroup(senderId = senderId, sessionId = sessionId, recipientGroupId = recipientId, content = content, typeContent = typeContent)
        } else {
            sdMessagesApi.sendMessageToFriend(senderId = senderId, sessionId = sessionId, recipientId = recipientId, content = content, typeContent = typeContent)
        }
        return sendMessage
                .doOnSuccess { date ->
                    debug("doOnSuccess sendMessage(senderId = $senderId, sessionId = $sessionId, recipientId = $recipientId, content = $content, typeContent = $typeContent, isRecipientAGroup = $isRecipientAGroup) = $date")
                    saveMessage(Message(content = content, senderId = senderId, recipientId = recipientId, typeContent = typeContent, dateCreated = date.toLong())).blockingAwait()
                }
                .doOnError { error("doOnError sendMessage(senderId = $senderId, sessionId = $sessionId, recipientId = $recipientId, content = $content, typeContent = $typeContent, isRecipientAGroup = $isRecipientAGroup) = ${it.message}") }
    }

    override fun deleteMessageFromServer(userId: String, sessionId: String, friendUserId: String, lastMessageViewedDate: Long): Single<String> {
        return sdMessagesApi.deleteMessageFromServer(userId, sessionId, friendUserId, lastMessageViewedDate.toString())
                .doOnSuccess { debug("doOnSuccess deleteMessageFromServer($userId, $sessionId, $friendUserId, $lastMessageViewedDate) = $it") }
                .doOnError { error("doOnError deleteMessageFromServer($userId, $sessionId, $friendUserId, $lastMessageViewedDate) = ${it.message}") }
    }

    override fun saveMessage(message: Message): Completable {
        return Completable.fromAction { messageDao.insert(message) }
                .doOnComplete { debug("doOnComplete saveTempSendingMessage($message)") }
                .doOnError { error("doOnError saveTempSendingMessage($message) = ${it.message}") }
    }

    override fun updateMessage(message: Message): Completable {
        return Completable.fromAction { messageDao.update(message) }
                .doOnComplete { debug("doOnComplete updateMessage($message)") }
                .doOnError { error("doOnError updateMessage($message) = ${it.message}") }
    }

    override fun findAllMessages(): Flowable<List<Message>> {
        return messageDao.findMessages()
                .doOnNext { debug("doOnNext findAllMessages() = $it") }
                .doOnError { error("doOnError findAllMessages(${it.message})") }
    }

    override fun updateMessagesWith1SecondMore(): Completable {
        return Completable.fromAction { /*messageDao.updateMessagesWith1SecondMore()*/ }
                .doOnComplete { debug("doOnComplete updateMessagesWith1SecondMore()") }
                .doOnError { error("doOnError updateMessagesWith1SecondMore() = ${it.message}") }
    }

    override fun deleteViewedMessages(): Completable {
        return Completable.fromAction { /*messageDao.deleteViewedMessages()*/ }
                .doOnComplete { debug("doOnComplete deleteViewedMessages()") }
                .doOnError { error("doOnError deleteViewedMessages() = ${it.message}") }
    }

}