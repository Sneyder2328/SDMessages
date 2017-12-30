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

package com.sneyder.sdmessages.ui.main.chats

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.utils.CircleImageView
import com.squareup.picasso.Picasso

class ChatsAdapter(private val context: Context): RecyclerView.Adapter<ChatsAdapter.ChatRoomViewHolder>() {

    var friends: List<UserInfo> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ChatRoomViewHolder?, position: Int) {
        holder?.bind(friends[holder.adapterPosition])
    }

    override fun getItemCount(): Int = friends.count()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.fragment_chats_item, parent, false)
        return ChatRoomViewHolder(view, context)
    }

    class ChatRoomViewHolder(
            private val view: View,
            private val context: Context
    ): RecyclerView.ViewHolder(view) {

        private val nameTextView: TextView by lazy { view.findViewById<TextView>(R.id.nameTextView) }
        private val pictureImageView: CircleImageView by lazy { view.findViewById<CircleImageView>(R.id.pictureImageView) }

        fun bind(friend: UserInfo) {
            nameTextView.text = friend.displayName
            Picasso.with(context).load(friend.photoUrl).into(pictureImageView)
        }

    }
}
