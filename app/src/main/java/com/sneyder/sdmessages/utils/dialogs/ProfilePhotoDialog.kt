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
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.ListView
import com.sneyder.sdmessages.R
import android.widget.TextView
import android.content.Context
import android.support.annotation.DrawableRes
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView


class ProfilePhotoDialog: BottomSheetDialogFragment() {

    companion object {

        fun newInstance(): ProfilePhotoDialog {
            return ProfilePhotoDialog()
        }

    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        val context = context
        if(dialog==null || context == null) return
        val contentView = View.inflate(context, R.layout.dialog_profile_photo, null)
        val listView = contentView.findViewById<ListView>(R.id.photoActionsListView)
        val actions = listOf(
                Action(R.drawable.ic_photo_gallery, getString(R.string.dialog_profile_photo_action_gallery)),
                Action(R.drawable.ic_camera, getString(R.string.dialog_profile_photo_action_camera)),
                Action(R.drawable.ic_camera, getString(R.string.dialog_profile_photo_action_url))
        )
        listView.adapter = ListAdapter(context, R.layout.dialog_profile_photo_action_item, actions)
        listView.dividerHeight = 0
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> getRegisteredListener()?.onPickImage()
                1 -> getRegisteredListener()?.onTakePicture()
                2 -> getRegisteredListener()?.onShowAddUrlImageDialog()
            }
            dialog.cancel()
        }
        dialog.setContentView(contentView)
    }

    private fun getRegisteredListener(): SelectImageListener? {
        return when {
            parentFragment is SelectImageListener -> parentFragment as SelectImageListener
            context is SelectImageListener -> context as SelectImageListener
            activity is SelectImageListener -> activity as SelectImageListener
            else -> throw NoOnDateSetListenerException()
        }
    }

    interface SelectImageListener {
        fun onTakePicture()
        fun onPickImage()
        fun onShowAddUrlImageDialog()
    }

    data class Action(@DrawableRes val icon: Int, val title: String)

    class ListAdapter(context: Context, resource: Int, items: List<Action>) : ArrayAdapter<Action>(context, resource, items) {

        @SuppressLint("InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var v: View? = convertView

            if (v == null) {
                val vi: LayoutInflater = LayoutInflater.from(context)
                v = vi.inflate(R.layout.dialog_profile_photo_action_item, null)
            }

            val item = getItem(position)

            val iconImageView: ImageView = v!!.findViewById(R.id.iconImageView)
            iconImageView.setImageResource(item.icon)
            val actionTextView: TextView = v.findViewById(R.id.actionTextView)
            actionTextView.text = item.title

            return v
        }
    }
}