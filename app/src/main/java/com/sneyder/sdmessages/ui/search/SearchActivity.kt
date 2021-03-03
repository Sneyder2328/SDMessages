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

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.Contact
import com.sneyder.sdmessages.data.model.UserInfo
import com.sneyder.sdmessages.data.model.asContact
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.ui.conversation.ConversationActivity
import com.sneyder.sdmessages.utils.RxSearchObservable
import com.sneyder.sdmessages.utils.dialogs.SendFriendRequestDialog
import com.sneyder.sdmessages.utils.ifSuccess
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import debug
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchActivity : DaggerActivity(), HasSupportFragmentInjector, SendFriendRequestDialog.SendFriendRequestListener  {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }

    }

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    private val searchPeopleViewModel by lazy { getViewModel<SearchViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initAWSMobileClient()

        searchPeopleViewModel.people.observe(this, Observer { people ->
            people.ifSuccess {
                debug("searchPeopleViewModel.people.reObserve success $it")
                peopleAdapter.people = it
            }
        })
        setUpPeopleRecyclerView()

        RxSearchObservable.fromEditText(searchEditText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .map { it.trim() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { name: String ->
                    searchPeopleViewModel.searchPeopleByName(name)
                }
    }

    private val peopleAdapter by lazy {
        SearchAdapter(this, { userSelected ->
            debug("$userSelected has been selected")
           /* if (userSelected.a) {
                debug("showConversationWithFriend")*/
                showConversationWithFriend(userSelected)
          /*  } else {
                debug("showDialogSendFriendRequest")
                showDialogSendFriendRequest(userSelected)
            }*/
        })
    }

    private fun showConversationWithFriend(userSelected: Contact) {
        startActivity(ConversationActivity.starterIntent(this, userSelected))
    }

    private fun showDialogSendFriendRequest(userSelected: UserInfo) {
        SendFriendRequestDialog.newInstance(userSelected).show(supportFragmentManager, "SendRequestFragmentTag")
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
