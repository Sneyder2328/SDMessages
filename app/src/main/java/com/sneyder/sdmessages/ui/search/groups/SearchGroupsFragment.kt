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

package com.sneyder.sdmessages.ui.search.groups

import android.content.Context
import android.support.v4.app.Fragment
import com.sneyder.sdmessages.R

import com.sneyder.sdmessages.ui.base.DaggerFragment
import com.sneyder.sdmessages.ui.search.SearchListener
import debug

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchGroupsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SearchGroupsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchGroupsFragment : DaggerFragment(), SearchListener {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchGroupsFragment.
         */
        fun newInstance(): SearchGroupsFragment {
            return SearchGroupsFragment()
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_search_groups

    private val searchGroupsViewModel by lazy { getViewModel(SearchGroupsViewModel::class.java) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
/*        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        //mListener = null
    }

    override fun search(name: String) {
        debug("search groups $name")

    }
}
