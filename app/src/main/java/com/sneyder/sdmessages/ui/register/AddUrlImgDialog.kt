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

package com.sneyder.sdmessages.ui.register

import addTextChangedListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.utils.CircleImageView
import com.squareup.picasso.Picasso

class AddUrlImgDialog : DialogFragment() {

    companion object {

        fun newInstance(): AddUrlImgDialog {
            return AddUrlImgDialog()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_register_add_image_url_dialog, null)

        val imagePreviewImageView: CircleImageView = v.findViewById(R.id.imagePreviewImageView)
        val imageUrlEditText: EditText = v.findViewById(R.id.imageUrlEditText)
        imageUrlEditText.addTextChangedListener({
            val url = it.toString()
            if(url.isNotBlank()) Picasso.with(context).load(url).into(imagePreviewImageView)
        })

        return AlertDialog.Builder(context!!)
                .setView(v)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val url = imageUrlEditText.text.toString()
                    if(url.isNotBlank()) getRegisteredListener().onUrlImgAdded(url)
                }
                .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _ -> dialog.cancel() }
                .create()
    }

    private fun getRegisteredListener(): AddUrlImgListener {
        return when {
            parentFragment is AddUrlImgListener -> parentFragment as AddUrlImgListener
            activity is AddUrlImgListener -> activity as AddUrlImgListener
            else -> context as AddUrlImgListener
        }
    }

    interface AddUrlImgListener {
        fun onUrlImgAdded(urlImg: String)
    }
}