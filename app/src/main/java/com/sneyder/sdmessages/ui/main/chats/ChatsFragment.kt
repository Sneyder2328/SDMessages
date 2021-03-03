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

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.Contact
import com.sneyder.sdmessages.ui.base.DaggerFragment
import com.sneyder.sdmessages.ui.conversation.ConversationActivity
import com.sneyder.sdmessages.utils.ifSuccess
import debug
import kotlinx.android.synthetic.main.fragment_chats.*
import reObserve

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsFragment : DaggerFragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment
         * @return A new instance of fragment ChatsFragment.
         */
        fun newInstance(): ChatsFragment {
            return ChatsFragment()
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_chats

    private val chatsViewModel by lazy { getViewModel<ChatsViewModel>() }

    private val chatRoomsAdapter by lazy { ChatsAdapter(context!!, friendSelectedListener) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        chatsViewModel.chatsLiveData.reObserve(this, Observer { contacts ->
            contacts?.ifSuccess { chatRoomsAdapter.contacts = it.filterNotNull() }
        })
        setUpChatsRecyclerView()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        loadChats()
        super.onResume()
    }

    fun loadChats(forceUpdate: Boolean = false) {
        debug("loadChats($forceUpdate)")
        chatsViewModel.loadChats(forceUpdate)
    }

    private fun setUpChatsRecyclerView() {
        chatsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = chatRoomsAdapter
            setHasFixedSize(true)
        }
    }

    private val friendSelectedListener = object : ChatsAdapter.ChatSelectedListener {
        override fun onChatSelected(contact: Contact) {
            activity?.startActivity(ConversationActivity.starterIntent(context!!, contact))
        }
    }
}
