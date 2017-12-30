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

package com.sneyder.sdmessages.ui.main.groups

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment

import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.data.model.ifSuccess
import com.sneyder.sdmessages.ui.base.DaggerFragment
import reObserve

/**
 * A simple [Fragment] subclass.
 * Use the [GroupsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupsFragment : DaggerFragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment
         * @return A new instance of fragment GroupsFragment.
         */
        fun newInstance(): GroupsFragment {
            return GroupsFragment()
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_groups

    private val groupsViewModel by lazy { getViewModel(GroupsViewModel::class.java) }
    private val groupsAdapter by lazy { GroupsAdapter(context!!) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        groupsViewModel.loadGroups()
        groupsViewModel.groups.reObserve(this, Observer { groups ->
            groups.ifSuccess { groupsAdapter.groups = it }
        })
        super.onActivityCreated(savedInstanceState)
    }
}
