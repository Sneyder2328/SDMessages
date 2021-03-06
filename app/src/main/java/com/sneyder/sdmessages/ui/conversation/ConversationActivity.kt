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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.base.DaggerActivity
import kotlinx.android.synthetic.main.activity_conversation.*
import addTextChangedListener
import android.app.Activity
import android.arch.lifecycle.Observer
import android.support.v7.widget.LinearLayoutManager
import com.sneyder.sdmessages.data.model.Contact
import com.sneyder.sdmessages.data.model.Message
import com.sneyder.sdmessages.data.model.TypeContent
import com.sneyder.sdmessages.data.rxbus.OneShotLiveDataBus
import com.sneyder.sdmessages.data.service.AppFirebaseMessagingService
import com.sneyder.sdmessages.ui.register.AddUrlImgDialog
import com.sneyder.sdmessages.utils.*
import com.sneyder.sdmessages.utils.dialogs.SelectImageDialog
import debug
import distinc
import notificationManager
import showValues
import toast
import java.io.File
import javax.inject.Inject


class ConversationActivity : DaggerActivity(), SelectImageDialog.SelectImageListener, AddUrlImgDialog.AddUrlImgListener {

    companion object {

        private const val REQUEST_SEND_IMAGE = 10

        private const val EXTRA_CONTACT = "contact"

        fun starterIntent(context: Context, contact: Contact): Intent {
            val starter = Intent(context, ConversationActivity::class.java)
            starter.putExtra(EXTRA_CONTACT, contact)
            return starter
        }

        private const val EXTRA_CONTENT_NEW_MESSAGE = "contentNewMessage"

        fun starterIntent(context: Context, newMessageData: AppFirebaseMessagingService.NewMessageData): Intent {
            val starter = Intent(context, ConversationActivity::class.java)
            starter.putExtra(EXTRA_CONTACT, Contact(id = newMessageData.fromUserId, pictureUrl = newMessageData.fromPhotoUrl, name = newMessageData.fromUserName, isGroup = false))
            starter.putExtra(EXTRA_CONTENT_NEW_MESSAGE, newMessageData.content)
            return starter
        }

    }

    @Inject lateinit var newMessagesBus: OneShotLiveDataBus<AppFirebaseMessagingService.NewMessageData>
    private val conversationViewModel by lazy { getViewModel<ConversationViewModel>() }
    private val messagesAdapter by lazy { ConversationAdapter(this, conversationViewModel) }
    private val contact: Contact by lazy { intent.getParcelableExtra<Contact>(EXTRA_CONTACT) }
    private var btnActionSend = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        initAWSMobileClient()

        setUpUi()

        observeMessages()

        observeSendMessageStatus()

        observeImagesUploading()

        loadMessages()

        if(intent.action == ACTION_REPLY_NEW_MESSAGE) notificationManager.cancel(NOTIFICATION_ID_NEW_MESSAGE)
        debug("ConversationActivity onCreate")
    }

    private fun setUpUi() {
        messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ConversationActivity, LinearLayoutManager.VERTICAL, false)
            adapter = messagesAdapter
        }
        newMessageEditText.addTextChangedListener({ text ->
            if (text.isNotBlank()) {
                btnActionSend = true
                actionButton.setImageResource(R.drawable.ic_send)
            } else {
                btnActionSend = false
                actionButton.setImageResource(R.drawable.ic_camera)
            }
        })

        //actionButton.setColorFilter(Color.parseColor("#757575"))
        actionButton.setOnClickListener {
            if (btnActionSend) {
                conversationViewModel.sendMessage(contact, newMessageEditText.text.toString(), TypeContent.TEXT.data)
            } else {
                showSelectImgDialog()
            }
        }
    }

    private fun observeImagesUploading() {
        conversationViewModel.imgsBeingUploaded.observe(this, Observer { it ->
            it?.forEach { img ->
                debug("forEach img key = ${img.key} img value data = ${img.value.data}")
                img.value.ifSuccess {
                    debug("ifSuccess img key = ${img.key} img value data = ${img.value.data}")
                    conversationViewModel.sendMessage(contact, img.key, TypeContent.IMAGE.data)
                }
                img.value.ifLoading {
                    debug("ifLoading img key = ${img.key} img value data = ${img.value.data}")
                }
                img.value.ifError {
                    debug("ifError img key = ${img.key} img value data = ${img.value.data}")
                }
            }
        })
    }

    private fun loadMessages() {
        conversationViewModel.loadMessages(contact)
    }

    private fun observeSendMessageStatus() {
        conversationViewModel.sendMessageStatus.observe(this, Observer { status ->
            status.ifLoading {
                newMessageEditText.isEnabled = false
                actionButton.isEnabled = false
            }
            status.ifSuccess {
                newMessageEditText.clearText()
                newMessageEditText.isEnabled = true
                actionButton.isEnabled = true
                loadMessages()
            }
            status.ifError {
                it?.let{ toast(it) }
                newMessageEditText.isEnabled = true
                actionButton.isEnabled = true
            }
        })
    }

    private var lastMessagesViewed: MutableList<Message> = ArrayList()


    private fun observeMessages() {
        conversationViewModel.messages.distinc().observe(this, Observer { resultMessages ->
            resultMessages.ifLoading { }
            resultMessages.ifSuccess { messages ->
                messages.showValues("resultMessages.ifSuccess messages")
                val newMessages = messages.filter { msg->
                    lastMessagesViewed.forEach {
                        if (it.dateCreated == msg.dateCreated) return@filter false
                    }
                    if(lastMessagesViewed.isEmpty()) return@filter true
                    return@filter true
                }
                newMessages.showValues("newMessages")
                newMessages.forEach {
                    messagesAdapter.addMessage(it)
                }/*
                val updatedMessages = messages.forEachIndexed { i, msg->
                    if(lastMessagesViewed.contains(msg)){
                        if(lastMessagesViewed.ge)
                    }
                }*/
                lastMessagesViewed.addAll(newMessages)
                lastMessagesViewed.showValues("lastMessagesViewed")
                scrollDownMessagesContainer()
            }
            resultMessages.ifError { it?.let{ toast(it) } }
        })
        newMessagesBus.subscribe(contact.id, this, Observer {
            debug("new message coming for ${contact.id}")
            loadMessages()
        })
    }

    private fun scrollDownMessagesContainer() {
        val index = messagesAdapter.messages.lastIndex
        debug("scrollDownMessagesContainer to $index")
        if(index  > 0) messagesRecyclerView.smoothScrollToPosition(messagesAdapter.messages.lastIndex)
    }

    private fun showSelectImgDialog() {
        SelectImageDialog.newInstance().show(supportFragmentManager, "SelectImageDialog")
    }

    override fun onTakePicture(){
        launchTakePictureIntent()
    }

    override fun onPickImage(){
        launchImageSelectorIntent()
    }

    override fun onShowAddUrlImageDialog(){
        AddUrlImgDialog.newInstance().show(supportFragmentManager, "AddUrlImgDialog")
    }

    override fun onUrlImgAdded(urlImg: String) {
        val intent = SendImageActivity.starterIntent(this, url = urlImg)
        startActivityForResult(intent, REQUEST_SEND_IMAGE)
    }

    override fun onActivityResultWithImageFile(imgPickedOrTaken: File) {
        val intent = SendImageActivity.starterIntent(this, path = imgPickedOrTaken.absolutePath)
        startActivityForResult(intent, REQUEST_SEND_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_SEND_IMAGE){
            val url = data?.getStringExtra(SendImageActivity.EXTRA_URL)
            val path = data?.getStringExtra(SendImageActivity.EXTRA_PATH)
            debug("onActivityResult url=$url path=$path")
            if (url!!.isNotBlank()){
                conversationViewModel.sendMessage(contact, url, TypeContent.IMAGE.data)
            }
            else {
                val bucketKey = conversationViewModel.uploadImage(path!!)
                conversationViewModel.saveTempSendingMessage(pathImg = path, bucketKey = bucketKey, typeContent = TypeContent.IMAGE_LOCAL.data, recipientId = contact.id)
            }
        }
    }
}