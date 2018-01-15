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

package com.sneyder.sdmessages.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.iid.FirebaseInstanceId
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.FriendRequest
import com.sneyder.sdmessages.data.rxbus.OneShotLiveDataBus
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.ui.home.HomeActivity
import com.sneyder.sdmessages.ui.search.SearchActivity
import com.sneyder.sdmessages.utils.*
import com.sneyder.sdmessages.utils.dialogs.ReceiveFriendRequestDialog
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import debug
import kotlinx.android.synthetic.main.activity_main.*
import notificationManager
import toast
import java.io.File
import javax.inject.Inject

class MainActivity : DaggerActivity(), HasSupportFragmentInjector, ReceiveFriendRequestDialog.ReceiveFriendRequestListener {

    companion object {

        const val FRIEND_REQUEST = "friendRequest"

        fun starterIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        fun starterIntentWithFriendRequest(context: Context, friendRequest: FriendRequest): Intent {
            return Intent(context, MainActivity::class.java).putExtra(FRIEND_REQUEST, friendRequest)
        }

    }

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var friendRequestsBus: OneShotLiveDataBus<FriendRequest>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    private val mainViewModel by lazy { getViewModel(MainViewModel::class.java) }
    private val mainPagerAdapter by lazy { MainPagerAdapter(this@MainActivity, supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAWSMobileClient()

        setSupportActionBar(toolbar)

        tabLayout.tabMode = TabLayout.MODE_FIXED
        viewPager.adapter = mainPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        mainViewModel.keepFirebaseTokenIdUpdated(FirebaseInstanceId.getInstance().token)
        subscribeToIncomingFriendRequests()
        manageFriendRequestIntent(intent)

        observeIncomingFriendRequestResult()
        observeLoggingOut()
    }


    private fun subscribeToIncomingFriendRequests() {
        friendRequestsBus.subscribe(lifecycleOwner = this, observer = Observer { friendRequest ->
            debug("friendRequestsBus onNext = $friendRequest")
            showFriendRequestDialog(friendRequest ?: return@Observer)
        })
    }

    private fun observeIncomingFriendRequestResult() {
        mainViewModel.incomingFriendRequest.observe(this, Observer {
            it.ifSuccess { mainPagerAdapter.chatsFragmentReference?.get()?.loadChats(true) }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        manageFriendRequestIntent(intent ?: return)
    }

    private fun manageFriendRequestIntent(intent: Intent) {
        val pendingFriendRequest: FriendRequest? = intent.getParcelableExtra(FRIEND_REQUEST)
        when (intent.action) {
            Action.ACCEPT_FRIEND_REQUEST -> mainViewModel.acceptFriendRequest(pendingFriendRequest)
            Action.REJECT_FRIEND_REQUEST -> mainViewModel.rejectFriendRequest(pendingFriendRequest)
            else -> friendRequestsBus.publish(message = pendingFriendRequest ?: return)
        }
        notificationManager.cancel(NOTIFICATION_ID_FRIEND_REQUESTS)
    }

    private fun showFriendRequestDialog(friendRequest: FriendRequest) {
        debug("showFriendRequestDialog $friendRequest")
        ReceiveFriendRequestDialog.newInstance(friendRequest).show(supportFragmentManager, "ReceiveFriendRequestDialog")
    }

    override fun onAccept(friendRequest: FriendRequest) {
        mainViewModel.acceptFriendRequest(friendRequest)
    }

    override fun onReject(friendRequest: FriendRequest) {
        mainViewModel.rejectFriendRequest(friendRequest)
    }

    override fun onActivityResultWithImageFile(imgPickedOrTaken: File) {
        mainPagerAdapter.profileFragmentReference?.get()?.onActivityResultWithImageFile(imgPickedOrTaken)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannels() {
        notificationManager.apply {
            val friendRequestsChannel = NotificationChannel(ID_CHANNEL_FRIEND_REQUESTS, NAME_CHANNEL_FRIEND_REQUESTS, NotificationManager.IMPORTANCE_HIGH)
            friendRequestsChannel.setShowBadge(false)
            createNotificationChannel(friendRequestsChannel)

            val newMessageChannel = NotificationChannel(ID_CHANNEL_NEW_MESSAGE, NAME_CHANNEL_NEW_MESSAGE, NotificationManager.IMPORTANCE_HIGH)
            newMessageChannel.setShowBadge(false)
            createNotificationChannel(newMessageChannel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_search -> openSearchActivity()
            R.id.action_log_out -> logOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openSearchActivity() {
        startActivity(SearchActivity.starterIntent(this@MainActivity))
    }

    private fun logOut() {
        mainViewModel.logOut()
    }

    private fun observeLoggingOut() {
        mainViewModel.loggedOut.observe(this, Observer {
            it.ifSuccess {
                startActivity(HomeActivity.starterIntent(this))
                finish()
            }
            it.ifError { it?.let { toast(it) } }
        })
    }
}
