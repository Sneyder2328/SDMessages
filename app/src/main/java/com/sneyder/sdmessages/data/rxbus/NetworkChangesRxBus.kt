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

package com.sneyder.sdmessages.data.rxbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import debug
import java.io.IOException
import com.sneyder.sdmessages.data.model.NetworkChangeStatus

/*
class NetworkChangesRxBus(private val context: Context): RxBus<NetworkChangeStatus>() {

    private var broadcastReceiver: BroadcastReceiver? = null
    private var listeningForNetworkChanges = false
    private var subscribers = 0

    private fun isInternetOn(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            //val ipProcess = runtime.exec("/system/bin/ping -c 1 sneyder.net") String command = "ping -c 1 google.com";
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    fun subscribeIfNecessary(){
        ++subscribers
        debug("subscribeIfNecessary $subscribers")
        if(!listeningForNetworkChanges)
            registerBroadCastReceiver()
    }

    fun unsubscribeIfNecessary() {
        --subscribers
        debug("unsubscribeIfNecessary $subscribers")
        if(subscribers <= 0 && listeningForNetworkChanges){
            unRegisterBroadCastReceiver()
        }
    }

    /**
     * Register for Internet connection change broadcast receiver
     */
    private fun registerBroadCastReceiver() {
        debug("registerBroadCastReceiver")
        listeningForNetworkChanges = true
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //val extras = intent.extras
                //val info = extras!!.getParcelable<NetworkInfo>("networkInfo")!!
                val networkStatus = if(isInternetOn()) NetworkChangeStatus.CONNECTED else NetworkChangeStatus.NO_CONNECTED
                debug("broadcastReceiver onReceive networkStatus = $networkStatus")
                bus.onNext(networkStatus)
            }
        }
        context.registerReceiver(broadcastReceiver, filter)
        bus.onNext(NetworkChangeStatus.BROADCAST_REGISTERED)
    }

    /**
     * unRegister for Internet connection change broadcast receiver
     */
    private fun unRegisterBroadCastReceiver() {
        debug("unRegisterBroadCastReceiver")
        listeningForNetworkChanges = false
        context.unregisterReceiver(broadcastReceiver)
        bus.onNext(NetworkChangeStatus.BROADCAST_UNREGISTERED)
    }
}*/