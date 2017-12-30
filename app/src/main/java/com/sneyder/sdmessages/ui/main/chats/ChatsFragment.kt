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
import com.sneyder.sdmessages.data.model.ifSuccess
import com.sneyder.sdmessages.ui.base.DaggerFragment
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

    private val chatsViewModel by lazy { getViewModel(ChatsViewModel::class.java) }

    private val chatRoomsAdapter by lazy { ChatsAdapter(context!!) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        chatsViewModel.loadChats()
        chatsViewModel.friends.reObserve(this, Observer { chatRooms ->
            chatRooms?.ifSuccess { chatRoomsAdapter.friends = it }
        })
        setUpChatsRecyclerView()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setUpChatsRecyclerView() {
        chatsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = chatRoomsAdapter
            setHasFixedSize(true)
        }
    }
}
