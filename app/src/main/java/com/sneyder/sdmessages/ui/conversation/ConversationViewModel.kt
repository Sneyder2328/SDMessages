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

package com.sneyder.sdmessages.ui.conversation

import android.arch.lifecycle.MutableLiveData
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.aws.S3FilesManager
import com.sneyder.sdmessages.data.model.Contact
import com.sneyder.sdmessages.data.model.Message
import com.sneyder.sdmessages.data.model.Resource
import com.sneyder.sdmessages.data.repository.MessageRepository
import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import com.sneyder.sdmessages.utils.SingleLiveEvent
import com.sneyder.sdmessages.utils.generateKeyForS3
import com.sneyder.sdmessages.utils.getS3BucketWithKey
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import debug
import io.reactivex.rxkotlin.subscribeBy
import showValues
import java.io.File
import javax.inject.Inject

data class TempMessage(
        var bucketKey: String = "",
        var pathImg: String,
        var messageId: Int = 0,
        var senderId: String = "",
        var recipientId: String = "",
        var typeContent: String = "",
        var dateCreated: Long = 0,
        var dateExpiry: Long = 0,
        var received: Boolean = false)

fun TempMessage.toMessage(): Message {
    return Message(content = pathImg, dateCreated = dateCreated, messageId = messageId, recipientId = recipientId, senderId = senderId, typeContent = typeContent, dateExpiry = dateExpiry, received = received)
}


class ConversationViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val s3FilesManager: S3FilesManager,
        private val messageRepository: MessageRepository,
        schedulerProvider: SchedulerProvider)
    : BaseViewModel(schedulerProvider) {

    val sendMessageStatus: SingleLiveEvent<Resource<String>> = SingleLiveEvent()
    private var isThereAnImgBeingUploaded = false

    fun sendMessage(contact: Contact, content: String, typeContent: String) {
        sendMessageStatus.value = Resource.loading()
        add(messageRepository.sendMessage(userRepository.getCurrentUserId(), userRepository.getCurrentSessionId(), recipientId = contact.id, content =  content, typeContent =  typeContent, isRecipientAGroup = false)
                .applySchedulers()
                .subscribeBy(
                        onSuccess = { sendMessageStatus.value = Resource.success(it) },
                        onError = { sendMessageStatus.value = Resource.error(R.string.conversation_error_sending_message) }
                ))
    }

    val messages: MutableLiveData<Resource<List<Message>>> = MutableLiveData()
    private var lastMessageIdReceived = 0

    fun loadMessages(contact: Contact) {
        debug("loadMessages($contact)")
        messages.value = Resource.loading()
        val loadMessages =
                if(contact.isGroup) messageRepository.findMessagesWithUserId(userRepository.getCurrentUserId(), userRepository.getCurrentSessionId(), contact.id)
                else messageRepository.findMessagesWithUserId(userRepository.getCurrentUserId(), userRepository.getCurrentSessionId(), contact.id)
        add(loadMessages
                .applySchedulers()
                .subscribeBy(
                        onNext = { resultMessages ->
                            resultMessages.showValues("resultMessages")
                            lastMessageIdReceived = resultMessages.maxBy { it.messageId }?.messageId ?: 0
                            resultMessages.forEach { msg ->
                                tempSendingMessages = tempSendingMessages.filterNot { it.bucketKey == msg.content }.toMutableList()
                            }
                            tempSendingMessages.showValues("tempSendingMessages")
                            messages.value = Resource.success(resultMessages.plus(tempSendingMessages.map { it.toMessage() }))
                        },
                        onError = { messages.value = Resource.error(R.string.conversation_error_loading_messages) }
                ))
    }

    val imgsBeingUploaded: SingleLiveEvent<Map<String, Resource<Int>>> = SingleLiveEvent()
    private val imgsBeingUploadedMap = HashMap<String, Resource<Int>>()

    fun uploadImage(path: String): String {
        val fileImg = File(path)
        val key = generateKeyForS3()
        val bucketWithKey = getS3BucketWithKey(key)
        imgsBeingUploadedMap[bucketWithKey] = Resource.loading()
        isThereAnImgBeingUploaded = true
        add(s3FilesManager.uploadFileCompressed(fileImg, key)
                .applySchedulers()
                .subscribeBy(
                        onNext = {
                            imgsBeingUploadedMap[bucketWithKey] = Resource.loading(it)
                            imgsBeingUploaded.value = imgsBeingUploadedMap
                        },
                        onComplete = {
                            imgsBeingUploadedMap[bucketWithKey] = Resource.success(100)
                            imgsBeingUploaded.value = imgsBeingUploadedMap
                            imgsBeingUploadedMap.remove(bucketWithKey)
                            debug("delete file ${fileImg.delete()}")
                        },
                        onError = {
                            imgsBeingUploadedMap[bucketWithKey] = Resource.error()
                            imgsBeingUploaded.value = imgsBeingUploadedMap
                            imgsBeingUploadedMap.remove(bucketWithKey)
                            debug("delete file ${fileImg.delete()}")
                        }
                ))
        return bucketWithKey
    }

    private var tempSendingMessages: MutableList<TempMessage> = ArrayList()

    fun saveTempSendingMessage(pathImg: String, bucketKey: String, typeContent: String, recipientId: String) {
        val message = TempMessage(pathImg = pathImg, bucketKey = bucketKey, typeContent = typeContent, senderId = userRepository.getCurrentUserId(), recipientId = recipientId)
        tempSendingMessages.add(message)
    }

    fun updateMessage(message: Message) {
        add(messageRepository.updateMessage(message)
                .applySchedulers()
                .subscribeBy(
                        onError = { },
                        onComplete = {}
                ))
    }
}