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

package com.sneyder.sdmessages.ui.main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.main.chats.ChatsFragment
import com.sneyder.sdmessages.ui.main.groups.GroupsFragment
import com.sneyder.sdmessages.ui.main.profile.ProfileFragment
import java.lang.ref.WeakReference

class MainPagerAdapter(context: Context, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    var chatsFragmentReference : WeakReference<ChatsFragment>? = null
    var profileFragmentReference : WeakReference<ProfileFragment>? = null

    private val titleFragments = context.resources.getStringArray(R.array.main_title_fragments)

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> ChatsFragment.newInstance()
            1 -> GroupsFragment.newInstance()
            else -> ProfileFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = titleFragments[position]

    override fun getCount(): Int = titleFragments.count()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        if(fragment is ChatsFragment) chatsFragmentReference = WeakReference(fragment)
        if(fragment is ProfileFragment) profileFragmentReference = WeakReference(fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        chatsFragmentReference?.clear()
        chatsFragmentReference = null
        profileFragmentReference?.clear()
        profileFragmentReference = null
        super.destroyItem(container, position, `object`)
    }

}