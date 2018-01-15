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

package com.sneyder.sdmessages.ui.search.people

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.ui.base.DaggerFragment
import com.sneyder.sdmessages.ui.search.SearchListener
import com.sneyder.sdmessages.utils.dialogs.SendFriendRequestDialog
import com.sneyder.sdmessages.utils.ifSuccess
import debug
import kotlinx.android.synthetic.main.fragment_search_people.*
import reObserve

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchPeopleFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SearchPeopleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchPeopleFragment : DaggerFragment(), SearchListener, SendFriendRequestDialog.SendFriendRequestListener {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPeopleFragment.
         */
        fun newInstance(): SearchPeopleFragment {
            return SearchPeopleFragment()
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_search_people

    private val searchPeopleViewModel by lazy { getViewModel(SearchPeopleViewModel::class.java) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        /*
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        //mListener = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        searchPeopleViewModel.people.reObserve(this, Observer { people ->
            people.ifSuccess {
                debug("searchPeopleViewModel.people.reObserve success $it")
                peopleAdapter.people = it
            }
        })
        setUpPeopleRecyclerView()
        super.onActivityCreated(savedInstanceState)
    }

    override fun search(name: String) {
        debug("search people $name")
        searchPeopleViewModel.searchPeopleByName(name)
    }

    private val peopleAdapter by lazy {
        PeopleAdapter(context!!, { userSelected->
            debug("$userSelected has been selected")
            showDialogSendFriendRequest(userSelected)
        })
    }

    private fun showDialogSendFriendRequest(userSelected: UserInfo) {
        SendFriendRequestDialog.newInstance(userSelected).show(childFragmentManager, "SendRequestFragmentTag")
    }

    override fun onSend(userSelected: UserInfo, message: String) {
        searchPeopleViewModel.sendFriendRequest(userSelected, message)
    }

    private fun setUpPeopleRecyclerView() {
        usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = peopleAdapter
            setHasFixedSize(true)
        }
    }
}
