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

package com.sneyder.sdmessages.ui.search

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.Contact
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.utils.CircleImageView
import into
import load

class SearchAdapter(
        private val context: Context,
        private val userSelectedListener: (Contact) -> Unit
): RecyclerView.Adapter<SearchAdapter.PeopleViewHolder>() {

    var people: List<Contact> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.fragment_search_people_item, parent, false)
        return PeopleViewHolder(view, context, userSelectedListener)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder?, position: Int) {
        holder?.bind(people[holder.adapterPosition])
    }


    override fun getItemCount(): Int = people.count()

    class PeopleViewHolder(
            private val view: View,
            private val appContext: Context,
            private val userSelectedListener: (Contact) -> Unit
    ): RecyclerView.ViewHolder(view) {

        private val nameTextView: TextView by lazy { view.findViewById<TextView>(R.id.nameTextView) }
        private val pictureImageView: CircleImageView by lazy { view.findViewById<CircleImageView>(R.id.pictureImageView) }

        fun bind(user: Contact){
            nameTextView.text = user.name
            pictureImageView.setImageResource(R.drawable.ic_user_img_profile)
            appContext.load(user.pictureUrl).into(pictureImageView, { fit().centerCrop()})
            view.setOnClickListener { userSelectedListener(user) }
        }
    }
}