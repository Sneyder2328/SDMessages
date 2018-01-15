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

class SelectImageDialog: DialogFragment() {

    companion object {

        fun newInstance(): SelectImageDialog {
            return SelectImageDialog()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!).setItems(R.array.register_add_profile_img_options, { _, item ->
            when (item) {
                0 -> getRegisteredListener().onTakePicture()
                1 -> getRegisteredListener().onPickImage()
                2 -> getRegisteredListener().onShowAddUrlImageDialog()
            }
        }).create()
    }

    private fun getRegisteredListener(): SelectImageListener {
        return when {
            parentFragment is SelectImageListener -> parentFragment as SelectImageListener
            activity is SelectImageListener -> activity as SelectImageListener
            else -> context as SelectImageListener
        }
    }

    interface SelectImageListener {
        fun onTakePicture()
        fun onPickImage()
        fun onShowAddUrlImageDialog()
    }

}