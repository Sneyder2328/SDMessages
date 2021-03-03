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

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import bundleOf
import com.sneyder.sdmessages.R

class EditNameDialog: DialogFragment() {

    companion object {

        private const val ARG_USER_NAME = "userName"

        fun newInstance(userName: String): EditNameDialog {
            return EditNameDialog().apply {
                arguments = bundleOf(ARG_USER_NAME to userName)
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val username = arguments?.getString(ARG_USER_NAME) ?: throw Exception("No value passed to argument ARG_USER_SELECTED in SendFriendRequestDialog")

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_edit_name, null)

        val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)
        usernameEditText.setText(username)

        return AlertDialog.Builder(context!!)
                .setView(view)
                .setTitle(String.format(getString(R.string.dialog_edit_name_title), username))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val newUserName = usernameEditText.text.toString().trim()
                    if(newUserName != username) getRegisteredListener()?.onNewUserName(newUserName)
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .create()
    }

    private fun getRegisteredListener(): EditNameListener? {
        return when {
            parentFragment is EditNameListener -> parentFragment as EditNameListener
            context is EditNameListener -> context as EditNameListener
            activity is EditNameListener -> activity as EditNameListener
            else -> throw NoOnDateSetListenerException()
        }
    }

    interface EditNameListener {
        fun onNewUserName(userName: String)
    }

}