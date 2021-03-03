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

package com.sneyder.sdmessages.utils.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.sneyder.sdmessages.R
import bundleOf
import com.sneyder.sdmessages.data.model.FriendRequest

class ReceiveFriendRequestDialog: DialogFragment() {

    companion object {

        private const val ARG_FRIEND_REQUEST = "friendRequest"

        fun newInstance(friendRequest: FriendRequest): ReceiveFriendRequestDialog {
            return ReceiveFriendRequestDialog().apply {
                arguments = bundleOf(ARG_FRIEND_REQUEST to friendRequest)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val friendRequest: FriendRequest = arguments?.getParcelable(ARG_FRIEND_REQUEST) ?: throw Exception("No value passed to argument ARG_FRIEND_REQUEST in ReceiveFriendRequestDialog")
        return AlertDialog.Builder(context!!)
                .setView(view)
                .setTitle(String.format(getString(R.string.dialog_receive_friend_request_title), friendRequest.fromUserName))
                .setMessage(friendRequest.message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_receive_friend_request_accept) { dialog, _ ->
                    dialog.cancel()
                    getRegisteredListener()?.onAccept(friendRequest)
                }
                .setNegativeButton(R.string.dialog_receive_friend_request_reject) { dialog, _ ->
                    dialog.cancel()
                    getRegisteredListener()?.onReject(friendRequest)
                }
                .create()
    }

    private fun getRegisteredListener(): ReceiveFriendRequestListener? {
        return when {
            parentFragment is ReceiveFriendRequestListener -> parentFragment as ReceiveFriendRequestListener
            context is ReceiveFriendRequestListener -> context as ReceiveFriendRequestListener
            activity is ReceiveFriendRequestListener -> activity as ReceiveFriendRequestListener
            else -> throw NoOnDateSetListenerException()
        }
    }

    interface ReceiveFriendRequestListener {
        fun onAccept(friendRequest: FriendRequest)
        fun onReject(friendRequest: FriendRequest)
    }
}