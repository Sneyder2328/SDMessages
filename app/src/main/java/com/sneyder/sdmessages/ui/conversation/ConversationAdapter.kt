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
import com.sneyder.sdmessages.data.model.Message
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.TypeContent
import com.sneyder.sdmessages.ui.main.profile.BlurTransformation
import com.sneyder.sdmessages.utils.asS3UrlIfApplicable
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import debug
import inflate
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import screenHeight
import screenWidth
import setDimensions
import java.io.File

class ConversationAdapter(
        private val activityContext: Context,
        private val conversationViewModel: ConversationViewModel
) : RecyclerView.Adapter<ConversationAdapter.MessageViewHolder>() {

    companion object {
        const val SENT_MESSAGE = 1
        const val RECEIVED_MESSAGE = 2
    }

    var messages: MutableList<Message> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addMessage(message: Message){
        messages.add(message)
        notifyItemInserted(messages.lastIndex)
    }

    override fun onBindViewHolder(holder: MessageViewHolder?, position: Int) {
        debug("onBindViewHolder $position")
        holder?.bind(messages[holder.adapterPosition])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder {
        val view = if(viewType == RECEIVED_MESSAGE) parent!!.inflate(R.layout.activity_conversation_message_in_item, false)
        else parent!!.inflate(R.layout.activity_conversation_message_out_item, false)
        return MessageViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].received) {
            RECEIVED_MESSAGE
        } else SENT_MESSAGE
    }

    override fun getItemCount(): Int = messages.count()

    inner class MessageViewHolder(
            private val view: View
    ) : RecyclerView.ViewHolder(view) {

       // private val imgDownloadProgressBar: ProgressBar by lazy { view.findViewById<ProgressBar>(R.id.imgDownloadProgressBar) }
       // private val imgUploadProgressBar: ProgressBar? by lazy { view.findViewById<ProgressBar>(R.id.imgUploadProgressBar) }
        private val contentImageView: ImageView by lazy { view.findViewById<ImageView>(R.id.contentImageView) }
        private val contentTextView: TextView by lazy { view.findViewById<TextView>(R.id.contentTextView) }

        fun bind(message: Message) {
            val size = (Math.min(activityContext.screenWidth(), activityContext.screenHeight()) * 0.70).toInt()
            contentImageView.setDimensions(size, size)
            contentImageView.setOnClickListener {
                activityContext.startActivity(ViewPictureActivity.starterIntent(activityContext, message.content))
            }
            when {
                message.typeContent == TypeContent.TEXT.data -> {
                    contentTextView.text = message.content
                    contentImageView.visibility = View.GONE
                }
                message.typeContent == TypeContent.IMAGE.data -> {
                    contentTextView.visibility = View.GONE
                    launch(UI) {
                        val url = message.content.asS3UrlIfApplicable().await()
                        if (url != message.content) {
                            message.content = url
                            conversationViewModel.updateMessage(message)
                        }
                        Picasso.with(activityContext).load(url).resize(40, 40).transform(BlurTransformation(activityContext))
                                .noFade().into(contentImageView, object : Callback {
                            override fun onSuccess() {
                                debug("load img onSuccess")
                                Picasso.with(activityContext).load(url).fit().centerCrop().into(contentImageView)
                            }
                            override fun onError() {}
                        })
                    }
                }
                message.typeContent == TypeContent.IMAGE_LOCAL.data -> {
                    contentTextView.visibility = View.GONE
                    Picasso.with(activityContext).load(File(message.content)).fit().centerCrop().into(contentImageView)
                }
            }
        }

    }

}
