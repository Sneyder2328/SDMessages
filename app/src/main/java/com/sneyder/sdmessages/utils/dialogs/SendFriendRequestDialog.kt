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
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.sneyder.sdmessages.R
import bundleOf
import com.sneyder.sdmessages.data.model.UserInfo


class SendFriendRequestDialog: DialogFragment() {

    companion object {

        private const val ARG_USER_SELECTED = "userSelected"

        fun newInstance(userSelected: UserInfo): SendFriendRequestDialog {
            return SendFriendRequestDialog().apply {
                arguments = bundleOf(ARG_USER_SELECTED to userSelected)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val userSelected: UserInfo = arguments?.getParcelable(ARG_USER_SELECTED) ?: throw Exception("No value passed to argument ARG_USER_SELECTED in SendFriendRequestDialog")

        val view: View = LayoutInflater.from(context).inflate(R.layout.fragment_search_people_send_friend_request_dialog, null)

        val messageEditText = view.findViewById<EditText>(R.id.messageEditText)

        return AlertDialog.Builder(context!!)
                .setView(view)
                .setTitle(String.format(getString(R.string.dialog_send_friend_request_title), userSelected.displayName))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    getRegisteredListener()?.onSend(userSelected, messageEditText.text.toString())
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .create()
    }

    private fun getRegisteredListener(): SendFriendRequestListener? {
        return when {
            parentFragment is SendFriendRequestListener -> parentFragment as SendFriendRequestListener
            context is SendFriendRequestListener -> context as SendFriendRequestListener
            activity is SendFriendRequestListener -> activity as SendFriendRequestListener
            else -> null
        }
    }

    interface SendFriendRequestListener {
        fun onSend(userSelected: UserInfo, message: String)
    }

}