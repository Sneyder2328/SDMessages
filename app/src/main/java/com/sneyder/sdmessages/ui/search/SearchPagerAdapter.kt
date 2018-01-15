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

package com.sneyder.sdmessages.ui.search

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.search.groups.SearchGroupsFragment
import com.sneyder.sdmessages.ui.search.people.SearchPeopleFragment

class SearchPagerAdapter(context: Context, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    val registeredSearchListeners = SparseArray<SearchListener?>()

    private val titleFragments = context.resources.getStringArray(R.array.search_title_fragments)

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> SearchPeopleFragment.newInstance()
            else -> SearchGroupsFragment.newInstance()
        }
    }

    override fun getCount(): Int = titleFragments.count()

    override fun getPageTitle(position: Int): CharSequence? = titleFragments[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        registeredSearchListeners.put(position, fragment as SearchListener)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredSearchListeners.remove(position)
        super.destroyItem(container, position, `object`)
    }
}